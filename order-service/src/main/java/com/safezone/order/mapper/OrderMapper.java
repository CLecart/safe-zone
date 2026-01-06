package com.safezone.order.mapper;

import com.safezone.order.dto.OrderItemResponse;
import com.safezone.order.dto.OrderResponse;
import com.safezone.order.entity.Order;
import com.safezone.order.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * MapStruct mapper for Order entity and DTO conversions.
 * Provides compile-time generated mapping implementations.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-06
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    /**
     * Converts an Order entity to a response DTO.
     *
     * @param order the Order entity
     * @return the mapped OrderResponse
     */
    OrderResponse toResponse(Order order);

    /**
     * Converts an OrderItem entity to a response DTO.
     *
     * @param orderItem the OrderItem entity
     * @return the mapped OrderItemResponse
     */
    OrderItemResponse toItemResponse(OrderItem orderItem);

    /**
     * Converts a list of Order entities to response DTOs.
     *
     * @param orders the list of Order entities
     * @return the list of OrderResponse DTOs
     */
    List<OrderResponse> toResponseList(List<Order> orders);
}
