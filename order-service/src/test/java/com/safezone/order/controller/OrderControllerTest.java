package com.safezone.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safezone.common.security.JwtTokenProvider;
import com.safezone.order.dto.CreateOrderRequest;
import com.safezone.order.dto.OrderItemRequest;
import com.safezone.order.dto.OrderItemResponse;
import com.safezone.order.dto.OrderResponse;
import com.safezone.order.entity.OrderStatus;
import com.safezone.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@DisplayName("Order Controller Tests")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private OrderResponse orderResponse;

    @BeforeEach
    void setUp() {
        OrderItemResponse itemResponse = new OrderItemResponse(
                1L,
                1L,
                "Test Product",
                BigDecimal.valueOf(99.99),
                2,
                BigDecimal.valueOf(199.98)
        );

        orderResponse = new OrderResponse(
                1L,
                "ORD-12345678",
                1L,
                List.of(itemResponse),
                BigDecimal.valueOf(199.98),
                OrderStatus.PENDING,
                "123 Test Street",
                null,
                LocalDateTime.now(),
                null
        );
    }

    @Nested
    @DisplayName("Create Order Tests")
    @WithMockUser
    class CreateOrderTests {

        @Test
        @DisplayName("Should create order successfully")
        void shouldCreateOrderSuccessfully() throws Exception {
            CreateOrderRequest request = new CreateOrderRequest(
                    1L,
                    List.of(new OrderItemRequest(1L, 2)),
                    "123 Test Street",
                    null
            );

            given(orderService.createOrder(any(CreateOrderRequest.class))).willReturn(orderResponse);

            mockMvc.perform(post("/api/orders")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.orderNumber").value("ORD-12345678"));
        }

        @Test
        @DisplayName("Should reject invalid order request")
        void shouldRejectInvalidOrderRequest() throws Exception {
            CreateOrderRequest request = new CreateOrderRequest(
                    null,
                    List.of(),
                    "",
                    null
            );

            mockMvc.perform(post("/api/orders")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Get Order Tests")
    @WithMockUser
    class GetOrderTests {

        @Test
        @DisplayName("Should get order by ID")
        void shouldGetOrderById() throws Exception {
            given(orderService.getOrderById(1L)).willReturn(orderResponse);

            mockMvc.perform(get("/api/orders/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(1));
        }

        @Test
        @DisplayName("Should get orders by user ID")
        void shouldGetOrdersByUserId() throws Exception {
            var page = new PageImpl<>(List.of(orderResponse), PageRequest.of(0, 10), 1);
            given(orderService.getOrdersByUserId(eq(1L), any())).willReturn(page);

            mockMvc.perform(get("/api/orders/user/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    @Nested
    @DisplayName("Update Order Tests")
    @WithMockUser(roles = "ADMIN")
    class UpdateOrderTests {

        @Test
        @DisplayName("Should update order status")
        void shouldUpdateOrderStatus() throws Exception {
            OrderResponse updatedResponse = new OrderResponse(
                    1L,
                    "ORD-12345678",
                    1L,
                    orderResponse.items(),
                    BigDecimal.valueOf(199.98),
                    OrderStatus.CONFIRMED,
                    "123 Test Street",
                    null,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            given(orderService.updateOrderStatus(eq(1L), eq(OrderStatus.CONFIRMED))).willReturn(updatedResponse);

            mockMvc.perform(put("/api/orders/1/status")
                            .with(csrf())
                            .param("status", "CONFIRMED"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.status").value("CONFIRMED"));
        }

        @Test
        @DisplayName("Should cancel order")
        void shouldCancelOrder() throws Exception {
            OrderResponse cancelledResponse = new OrderResponse(
                    1L,
                    "ORD-12345678",
                    1L,
                    orderResponse.items(),
                    BigDecimal.valueOf(199.98),
                    OrderStatus.CANCELLED,
                    "123 Test Street",
                    null,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            given(orderService.cancelOrder(1L)).willReturn(cancelledResponse);

            mockMvc.perform(put("/api/orders/1/cancel")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.status").value("CANCELLED"));
        }
    }
}
