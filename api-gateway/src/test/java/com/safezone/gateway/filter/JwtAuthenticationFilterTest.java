package com.safezone.gateway.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

class JwtAuthenticationFilterTest {

    private static final String ORDERS_PATH = "/api/v1/orders";

    private String generateBase64Secret(byte[] outBytes) {
        SecureRandom rnd = new SecureRandom();
        rnd.nextBytes(outBytes);
        return Base64.getEncoder().encodeToString(outBytes);
    }

    @Test
    void publicEndpointIsBypassed() {
        byte[] key = new byte[64];
        String secret = generateBase64Secret(key);
        com.safezone.common.security.JwtTokenProvider provider = new com.safezone.common.security.JwtTokenProvider(
                secret, 86400000L);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(provider);

        MockServerHttpRequest request = MockServerHttpRequest.get("/actuator/health").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());

        filter.apply(new JwtAuthenticationFilter.Config()).filter(exchange, chain).block();

        verify(chain, times(1)).filter(any());
    }

    @Test
    void missingAuthorizationHeaderReturnsUnauthorized() {
        byte[] key = new byte[64];
        String secret = generateBase64Secret(key);
        com.safezone.common.security.JwtTokenProvider provider = new com.safezone.common.security.JwtTokenProvider(
                secret, 86400000L);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(provider);

        MockServerHttpRequest request = MockServerHttpRequest.get(ORDERS_PATH).build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());

        filter.apply(new JwtAuthenticationFilter.Config()).filter(exchange, chain).block();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(any());
    }

    @Test
    void invalidTokenReturnsUnauthorized() {
        byte[] key = new byte[64];
        String secret = generateBase64Secret(key);
        com.safezone.common.security.JwtTokenProvider provider = new com.safezone.common.security.JwtTokenProvider(
                secret, 86400000L);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(provider);

        MockServerHttpRequest request = MockServerHttpRequest.get(ORDERS_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer not-a-valid-token")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());

        filter.apply(new JwtAuthenticationFilter.Config()).filter(exchange, chain).block();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(chain, never()).filter(any());
    }

    @Test
    @SuppressWarnings("squid:S1874")
    void validTokenAddsUserHeadersAndCallsChain() {
        byte[] key = new byte[64];
        String secret = generateBase64Secret(key);
        com.safezone.common.security.JwtTokenProvider provider = new com.safezone.common.security.JwtTokenProvider(
                secret, 86400000L);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(provider);

        // Use the provider to generate a token so signing key and format are consistent
        String token = provider.generateToken("42", List.of("ROLE_USER"));
        // Sanity check the provider validates its own token
        org.assertj.core.api.Assertions.assertThat(provider.validateToken(token)).isTrue();

        MockServerHttpRequest request = MockServerHttpRequest.get(ORDERS_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        GatewayFilterChain chain = mock(GatewayFilterChain.class);
        when(chain.filter(any())).then(invocation -> {
            ServerWebExchange ex = invocation.getArgument(0);
            assertThat(ex.getRequest().getHeaders().getFirst("X-User-Id")).isEqualTo("42");
            assertThat(ex.getRequest().getHeaders().getFirst("X-User-Roles")).contains("ROLE_USER");
            return Mono.empty();
        });

        filter.apply(new JwtAuthenticationFilter.Config()).filter(exchange, chain).block();

        verify(chain, times(1)).filter(any());
    }
}
