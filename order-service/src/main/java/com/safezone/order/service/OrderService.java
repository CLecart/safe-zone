package com.safezone.order.service;

import com.safezone.order.dto.CreateOrderRequest;
import com.safezone.order.dto.OrderResponse;
import com.safezone.order.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request);

    OrderResponse getOrderById(Long id);

    OrderResponse getOrderByNumber(String orderNumber);

    Page<OrderResponse> getAllOrders(Pageable pageable);

    Page<OrderResponse> getOrdersByUserId(Long userId, Pageable pageable);

    Page<OrderResponse> getOrdersByStatus(OrderStatus status, Pageable pageable);

    OrderResponse updateOrderStatus(Long id, OrderStatus status);

    OrderResponse cancelOrder(Long id);
}
