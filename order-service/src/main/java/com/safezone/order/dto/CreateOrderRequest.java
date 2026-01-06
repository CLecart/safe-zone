package com.safezone.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateOrderRequest(
        @NotNull(message = "User ID is required")
        Long userId,

        @NotEmpty(message = "Order must have at least one item")
        @Valid
        List<OrderItemRequest> items,

        @Size(max = 500, message = "Shipping address cannot exceed 500 characters")
        String shippingAddress,

        @Size(max = 500, message = "Billing address cannot exceed 500 characters")
        String billingAddress
) {}
