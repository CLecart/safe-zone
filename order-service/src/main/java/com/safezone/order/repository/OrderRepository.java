package com.safezone.order.repository;

import com.safezone.order.entity.Order;
import com.safezone.order.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for Order entities.
 * Provides CRUD operations and custom queries for order management.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Finds an order by its order number.
     *
     * @param orderNumber the unique order number
     * @return an Optional containing the order if found
     */
    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * Finds orders for a specific user with pagination.
     *
     * @param userId   the user ID
     * @param pageable pagination parameters
     * @return page of user's orders
     */
    Page<Order> findByUserId(Long userId, Pageable pageable);

    /**
     * Finds orders by status with pagination.
     *
     * @param status   the order status
     * @param pageable pagination parameters
     * @return page of orders with the given status
     */
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    /**
     * Finds orders for a user with a specific status.
     *
     * @param userId   the user ID
     * @param status   the order status
     * @param pageable pagination parameters
     * @return page of matching orders
     */
    Page<Order> findByUserIdAndStatus(Long userId, OrderStatus status, Pageable pageable);

    /**
     * Finds orders created within a date range.
     *
     * @param startDate start of the date range
     * @param endDate   end of the date range
     * @return list of orders in the date range
     */
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findOrdersBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Counts orders for a user with a specific status.
     *
     * @param userId the user ID
     * @param status the order status
     * @return count of matching orders
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.userId = :userId AND o.status = :status")
    Long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") OrderStatus status);

    /**
     * Checks if an order with the given order number exists.
     *
     * @param orderNumber the order number
     * @return true if an order with this number exists
     */
    boolean existsByOrderNumber(String orderNumber);
}
