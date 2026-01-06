package com.safezone.order.service;

import com.safezone.order.dto.CreateOrderRequest;
import com.safezone.order.dto.OrderResponse;
import com.safezone.order.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for order management operations.
 * Handles order lifecycle from creation through fulfillment and cancellation.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 */
public interface OrderService {

    /**
     * Creates a new order from the provided request.
     * Validates product availability and calculates total amount.
     *
     * @param request the order creation request with items and addresses
     * @return the created order response with generated order number
     * @throws com.safezone.common.exception.BusinessException if products unavailable
     */
    OrderResponse createOrder(CreateOrderRequest request);

    /**
     * Retrieves an order by its unique identifier.
     *
     * @param id the order ID
     * @return the order response
     * @throws com.safezone.common.exception.ResourceNotFoundException if order not found
     */
    OrderResponse getOrderById(Long id);

    /**
     * Retrieves an order by its order number.
     *
     * @param orderNumber the unique order number
     * @return the order response
     * @throws com.safezone.common.exception.ResourceNotFoundException if order not found
     */
    OrderResponse getOrderByNumber(String orderNumber);

    /**
     * Retrieves all orders with pagination support.
     *
     * @param pageable pagination parameters
     * @return a page of order responses
     */
    Page<OrderResponse> getAllOrders(Pageable pageable);

    /**
     * Retrieves all orders for a specific user.
     *
     * @param userId the user ID
     * @param pageable pagination parameters
     * @return a page of order responses for the user
     */
    Page<OrderResponse> getOrdersByUserId(Long userId, Pageable pageable);

    /**
     * Retrieves orders filtered by status.
     *
     * @param status the order status to filter by
     * @param pageable pagination parameters
     * @return a page of order responses with matching status
     */
    Page<OrderResponse> getOrdersByStatus(OrderStatus status, Pageable pageable);

    /**
     * Updates the status of an order.
     * Validates status transitions according to business rules.
     *
     * @param id the order ID
     * @param status the new order status
     * @return the updated order response
     * @throws com.safezone.common.exception.BusinessException if status transition invalid
     */
    OrderResponse updateOrderStatus(Long id, OrderStatus status);

    /**
     * Cancels an existing order.
     * Only orders in PENDING or CONFIRMED status can be cancelled.
     *
     * @param id the order ID to cancel
     * @return the cancelled order response
     * @throws com.safezone.common.exception.BusinessException if order cannot be cancelled
     */
    OrderResponse cancelOrder(Long id);
}
