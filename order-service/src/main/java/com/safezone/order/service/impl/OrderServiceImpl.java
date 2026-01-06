package com.safezone.order.service.impl;

import com.safezone.common.exception.BusinessException;
import com.safezone.common.exception.ResourceNotFoundException;
import com.safezone.order.client.ProductServiceClient;
import com.safezone.order.dto.CreateOrderRequest;
import com.safezone.order.dto.OrderItemRequest;
import com.safezone.order.dto.OrderResponse;
import com.safezone.order.dto.ProductDto;
import com.safezone.order.entity.Order;
import com.safezone.order.entity.OrderItem;
import com.safezone.order.entity.OrderStatus;
import com.safezone.order.mapper.OrderMapper;
import com.safezone.order.repository.OrderRepository;
import com.safezone.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    private static final String ORDER_RESOURCE = "Order";

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductServiceClient productServiceClient;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderMapper orderMapper,
            ProductServiceClient productServiceClient) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.productServiceClient = productServiceClient;
    }

    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {
        logger.info("Creating new order for user: {}", request.userId());

        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .userId(request.userId())
                .status(OrderStatus.PENDING)
                .shippingAddress(request.shippingAddress())
                .billingAddress(request.billingAddress())
                .build();

        for (OrderItemRequest itemRequest : request.items()) {
            OrderItem item = createOrderItem(itemRequest);
            order.addItem(item);
        }

        order.calculateTotalAmount();
        Order savedOrder = orderRepository.save(order);

        reserveStock(savedOrder);

        logger.info("Order created successfully with number: {}", savedOrder.getOrderNumber());
        return orderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        logger.debug("Fetching order by ID: {}", id);
        Order order = findOrderById(id);
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByNumber(String orderNumber) {
        logger.debug("Fetching order by number: {}", orderNumber);
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_RESOURCE, "orderNumber", orderNumber));
        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        logger.debug("Fetching all orders with pagination");
        return orderRepository.findAll(pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByUserId(Long userId, Pageable pageable) {
        logger.debug("Fetching orders for user: {}", userId);
        return orderRepository.findByUserId(userId, pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        logger.debug("Fetching orders by status: {}", status);
        return orderRepository.findByStatus(status, pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    public OrderResponse updateOrderStatus(Long id, OrderStatus status) {
        logger.info("Updating order {} status to: {}", id, status);

        Order order = findOrderById(id);
        validateStatusTransition(order.getStatus(), status);

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);

        logger.info("Order {} status updated to: {}", id, status);
        return orderMapper.toResponse(updatedOrder);
    }

    @Override
    public OrderResponse cancelOrder(Long id) {
        logger.info("Cancelling order: {}", id);

        Order order = findOrderById(id);

        if (!canBeCancelled(order.getStatus())) {
            throw new BusinessException("INVALID_STATUS",
                    "Order cannot be cancelled in status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order cancelledOrder = orderRepository.save(order);

        releaseStock(cancelledOrder);

        logger.info("Order {} cancelled successfully", id);
        return orderMapper.toResponse(cancelledOrder);
    }

    private Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ORDER_RESOURCE, "id", id));
    }

    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "ORD-" + timestamp + "-" + uuid;
    }

    private OrderItem createOrderItem(OrderItemRequest request) {
        ProductDto product = productServiceClient.getProductById(request.productId())
                .orElseThrow(() -> new BusinessException("PRODUCT_NOT_FOUND",
                        "Product not found with ID: " + request.productId()));

        if (!productServiceClient.checkProductAvailability(request.productId(), request.quantity())) {
            throw new BusinessException("INSUFFICIENT_STOCK",
                    "Insufficient stock for product: " + product.name());
        }

        OrderItem item = OrderItem.builder()
                .productId(product.id())
                .productName(product.name())
                .productSku(product.sku())
                .quantity(request.quantity())
                .unitPrice(product.price())
                .build();

        item.calculateSubtotal();
        return item;
    }

    private void reserveStock(Order order) {
        for (OrderItem item : order.getItems()) {
            productServiceClient.updateStock(item.getProductId(), -item.getQuantity())
                    .subscribe();
        }
    }

    private void releaseStock(Order order) {
        for (OrderItem item : order.getItems()) {
            productServiceClient.updateStock(item.getProductId(), item.getQuantity())
                    .subscribe();
        }
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus target) {
        if (current == OrderStatus.CANCELLED || current == OrderStatus.REFUNDED) {
            throw new BusinessException("INVALID_STATUS_TRANSITION",
                    "Cannot change status from " + current + " to " + target);
        }

        if (current == OrderStatus.DELIVERED && target != OrderStatus.REFUNDED) {
            throw new BusinessException("INVALID_STATUS_TRANSITION",
                    "Delivered order can only be refunded");
        }
    }

    private boolean canBeCancelled(OrderStatus status) {
        return status == OrderStatus.PENDING ||
                status == OrderStatus.CONFIRMED ||
                status == OrderStatus.PROCESSING;
    }
}
