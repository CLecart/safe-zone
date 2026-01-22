package com.safezone.order.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerSecurityIntegrationTest {
    @org.mockito.Mock
    private com.safezone.order.client.ProductServiceClient productServiceClient;
    @Autowired
    private com.safezone.order.repository.OrderRepository orderRepository;

    private Long testOrderId;

    @org.junit.jupiter.api.BeforeEach
    void setupTestOrder() {
        org.mockito.MockitoAnnotations.openMocks(this);
        // Mock du client produit pour éviter les appels HTTP réels
        org.mockito.Mockito.when(productServiceClient.getProductById(org.mockito.Mockito.anyLong()))
                .thenReturn(java.util.Optional.of(new com.safezone.order.dto.ProductDto(
                        1L,
                        "Produit Test",
                        "SKU-TEST-001",
                        java.math.BigDecimal.valueOf(100.00),
                        10,
                        true)));
        org.mockito.Mockito
                .when(productServiceClient.checkProductAvailability(org.mockito.Mockito.anyLong(),
                        org.mockito.Mockito.anyInt()))
                .thenReturn(true);
        // Supprimer toute commande de test existante pour éviter les doublons
        orderRepository.findByOrderNumber("TEST-ORDER-001").ifPresent(orderRepository::delete);
        com.safezone.order.entity.Order order = com.safezone.order.entity.Order.builder()
                .orderNumber("TEST-ORDER-001")
                .userId(1L)
                .status(com.safezone.order.entity.OrderStatus.PENDING)
                .shippingAddress("1 rue de test, Paris")
                .billingAddress("1 rue de test, Paris")
                .build();
        // Ajout d'un OrderItem de test
        com.safezone.order.entity.OrderItem item = com.safezone.order.entity.OrderItem.builder()
                .productId(1L)
                .productName("Produit Test")
                .productSku("SKU-TEST-001")
                .quantity(1)
                .unitPrice(java.math.BigDecimal.valueOf(100.00))
                .build();
        item.calculateSubtotal();
        order.addItem(item);
        order.calculateTotalAmount();
        testOrderId = orderRepository.save(order).getId();
    }

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/v1/orders/{id} doit être public")
    void getOrderById_publicAccess() throws Exception {
        mockMvc.perform(get("/api/v1/orders/" + testOrderId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/orders should require authentication")
    void getAllOrders_requiresAuth() throws Exception {
        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/v1/orders should require authentication")
    void createOrder_requiresAuth() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PATCH /api/v1/orders/{id}/status should require authentication")
    void updateOrderStatus_requiresAuth() throws Exception {
        mockMvc.perform(patch("/api/v1/orders/1/status")
                .param("status", "CONFIRMED"))
                .andExpect(status().isUnauthorized());
    }
}
