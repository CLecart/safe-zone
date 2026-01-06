package com.safezone.order.mapper;

import com.safezone.order.dto.OrderItemResponse;
import com.safezone.order.dto.OrderResponse;
import com.safezone.order.entity.Order;
import com.safezone.order.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    OrderResponse toResponse(Order order);

    OrderItemResponse toItemResponse(OrderItem orderItem);

    List<OrderResponse> toResponseList(List<Order> orders);
}
