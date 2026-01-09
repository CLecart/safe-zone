package com.safezone.order.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safezone.order.dto.CreateOrderRequest;
import com.safezone.order.dto.OrderItemRequest;
import com.safezone.order.dto.OrderResponse;
import com.safezone.order.entity.OrderStatus;
import com.safezone.order.service.OrderService;

/**
 * Integration tests for {@link OrderController}.
 * Tests all REST endpoints for order management with authentication and
 * authorization.
 *
 * @author SafeZone Team
 * @version 1.0.0
 * @since 2026-01-09
 */
@WebMvcTest(OrderController.class)
@DisplayName("OrderController Tests")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    private OrderResponse testOrderResponse;

    @BeforeEach
    void setUp() {
        testOrderResponse = new OrderResponse(
                1L,
                "ORD-20260106-ABC12345",
                1L,
                OrderStatus.PENDING,
                BigDecimal.valueOf(199.98),
                "123 Test St",
                null,
                new ArrayList<>(),
                LocalDateTime.now(),
                LocalDateTime.now());
    }

    @Nested
    @DisplayName("Create Order Tests")
    class CreateOrderTests {

        @Test
        @DisplayName("Should create order successfully")
        @WithMockUser(username = "user1", roles = "USER")
        void shouldCreateOrderSuccessfully() throws Exception {
            CreateOrderRequest request = new CreateOrderRequest(
                    1L,
                    List.of(new OrderItemRequest(1L, 2)),
                    "123 Test St",
                    null);

            given(orderService.createOrder(any(CreateOrderRequest.class)))
                    .willReturn(testOrderResponse);

            mockMvc.perform(post("/api/v1/orders")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.orderNumber").value("ORD-20260106-ABC12345"));
        }

        @Test
        @DisplayName("Should reject create order without authentication")
        void shouldRejectCreateOrderWithoutAuth() throws Exception {
            CreateOrderRequest request = new CreateOrderRequest(
                    1L,
                    List.of(new OrderItemRequest(1L, 2)),
                    "123 Test St",
                    null);

            mockMvc.perform(post("/api/v1/orders")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("Get Order Tests")
    @WithMockUser(username = "user1", roles = "USER")
    class GetOrderTests {

        @Test
        @DisplayName("Should get order by ID")
        void shouldGetOrderById() throws Exception {
            given(orderService.getOrderById(1L)).willReturn(testOrderResponse);

            mockMvc.perform(get("/api/v1/orders/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(1));
        }

        @Test
        @DisplayName("Should get order by order number")
        void shouldGetOrderByNumber() throws Exception {
            given(orderService.getOrderByNumber("ORD-20260106-ABC12345"))
                    .willReturn(testOrderResponse);

            mockMvc.perform(get("/api/v1/orders/number/ORD-20260106-ABC12345"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.orderNumber").value("ORD-20260106-ABC12345"));
        }

        @Test
        @DisplayName("Should get orders by user ID")
        void shouldGetOrdersByUserId() throws Exception {
            List<OrderResponse> orders = new ArrayList<>();
            orders.add(testOrderResponse);
            Pageable pageable = PageRequest.of(0, 20);
            var page = new PageImpl<>(orders, pageable, 1);

            given(orderService.getOrdersByUserId(anyLong(), any(Pageable.class))).willReturn(page);

            mockMvc.perform(get("/api/v1/orders/user/1")
                    .param("page", "0")
                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content", hasSize(1)));
        }
    }

    @Nested
    @DisplayName("Get All Orders Tests")
    @WithMockUser(username = "admin", roles = "ADMIN")
    class GetAllOrdersTests {

        @Test
        @DisplayName("Should get all orders with pagination")
        void shouldGetAllOrders() throws Exception {
            List<OrderResponse> orders = new ArrayList<>();
            orders.add(testOrderResponse);
            Pageable pageable = PageRequest.of(0, 20);
            var page = new PageImpl<>(orders, pageable, 1);

            given(orderService.getAllOrders(any(Pageable.class))).willReturn(page);

            mockMvc.perform(get("/api/v1/orders")
                    .param("page", "0")
                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content", hasSize(1)));
        }

        @Test
        @DisplayName("Should get all orders with descending sort")
        void shouldGetAllOrdersWithDescSort() throws Exception {
            List<OrderResponse> orders = new ArrayList<>();
            orders.add(testOrderResponse);
            Pageable pageable = PageRequest.of(0, 20);
            var page = new PageImpl<>(orders, pageable, 1);

            given(orderService.getAllOrders(any(Pageable.class))).willReturn(page);

            mockMvc.perform(get("/api/v1/orders")
                    .param("sortDir", "desc"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should get all orders with ascending sort")
        void shouldGetAllOrdersWithAscSort() throws Exception {
            List<OrderResponse> orders = new ArrayList<>();
            orders.add(testOrderResponse);
            Pageable pageable = PageRequest.of(0, 20);
            var page = new PageImpl<>(orders, pageable, 1);

            given(orderService.getAllOrders(any(Pageable.class))).willReturn(page);

            mockMvc.perform(get("/api/v1/orders")
                    .param("sortDir", "asc"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Should get orders by status")
        void shouldGetOrdersByStatus() throws Exception {
            List<OrderResponse> orders = new ArrayList<>();
            orders.add(testOrderResponse);
            Pageable pageable = PageRequest.of(0, 20);
            var page = new PageImpl<>(orders, pageable, 1);

            given(orderService.getOrdersByStatus(any(OrderStatus.class), any(Pageable.class)))
                    .willReturn(page);

            mockMvc.perform(get("/api/v1/orders/status/PENDING")
                    .param("page", "0")
                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true));
        }
    }

    @Nested
    @DisplayName("Update Order Status Tests")
    @WithMockUser(username = "admin", roles = "ADMIN")
    class UpdateOrderStatusTests {

        @Test
        @DisplayName("Should update order status")
        void shouldUpdateOrderStatus() throws Exception {
            OrderResponse updated = new OrderResponse(
                    1L,
                    "ORD-20260106-ABC12345",
                    1L,
                    OrderStatus.CONFIRMED,
                    BigDecimal.valueOf(199.98),
                    "123 Test St",
                    null,
                    new ArrayList<>(),
                    LocalDateTime.now(),
                    LocalDateTime.now());

            given(orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED))
                    .willReturn(updated);

            mockMvc.perform(patch("/api/v1/orders/1/status")
                    .with(csrf())
                    .param("status", "CONFIRMED"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.status").value("CONFIRMED"));
        }
    }

    @Nested
    @DisplayName("Cancel Order Tests")
    @WithMockUser(username = "user1", roles = "USER")
    class CancelOrderTests {

        @Test
        @DisplayName("Should cancel order")
        void shouldCancelOrder() throws Exception {
            OrderResponse cancelled = new OrderResponse(
                    1L,
                    "ORD-20260106-ABC12345",
                    1L,
                    OrderStatus.CANCELLED,
                    BigDecimal.valueOf(199.98),
                    "123 Test St",
                    null,
                    new ArrayList<>(),
                    LocalDateTime.now(),
                    LocalDateTime.now());

            given(orderService.cancelOrder(1L)).willReturn(cancelled);

            mockMvc.perform(post("/api/v1/orders/1/cancel")
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.status").value("CANCELLED"));
        }
    }
}
