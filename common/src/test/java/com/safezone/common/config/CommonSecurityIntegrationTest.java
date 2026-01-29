package com.safezone.common.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.cors.CorsConfigurationSource;

import com.safezone.common.security.JwtTokenProvider;

@SpringBootTest(classes = CommonSecurityIntegrationTest.TestConfig.class)
@AutoConfigureMockMvc
class CommonSecurityIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Test
    void getIsAllowedWithoutCsrf() throws Exception {
        mvc.perform(get("/api/test")).andExpect(status().isOk());
    }

    @Test
    void postWithoutAuthOrCookies_isForbiddenDueToMissingCsrf() throws Exception {
        mvc.perform(post("/api/test").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
    }

    @Test
    void postWithAuthorization_isAllowed() throws Exception {
        String token = jwtTokenProvider.generateToken("testuser", java.util.List.of("ROLE_USER"));
        mvc.perform(
                post("/api/test").header("Authorization", "Bearer " + token).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void postWithCookies_isForbiddenDueToCsrf() throws Exception {
        mvc.perform(post("/api/test").cookie(new jakarta.servlet.http.Cookie("JSESSIONID", "abc"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @org.springframework.boot.SpringBootConfiguration
    @org.springframework.boot.autoconfigure.EnableAutoConfiguration
    static class TestConfig {

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
            org.springframework.web.cors.CorsConfiguration config = new org.springframework.web.cors.CorsConfiguration();
            config.setAllowedOrigins(java.util.List.of("http://localhost:3000", "http://127.0.0.1:3000"));
            config.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
            config.setAllowedHeaders(java.util.List.of("*"));
            config.setExposedHeaders(java.util.List.of("Authorization", "Content-Type"));
            config.setAllowCredentials(false);
            config.setMaxAge(3600L);
            org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", config);
            return source;
        }

        @Bean
        public JwtTokenProvider jwtTokenProvider() {
            // return a minimal provider; not used for the test authorization check
            return new JwtTokenProvider("test-secret-long-enough-for-hmac-sha-256-test", 3600000L);
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtTokenProvider jwtTokenProvider,
                CorsConfigurationSource corsConfigurationSource) throws Exception {
            HttpSecurity h = CommonSecurityConfigurer.applyDefaultSecurity(http, jwtTokenProvider,
                    corsConfigurationSource);
            // allow this test endpoint to be accessed so we can assert CSRF behavior
            // independently
            h.authorizeHttpRequests(auth -> auth.requestMatchers("/api/test").permitAll());
            return h.build();
        }

        @RestController
        static class TestController {
            @PostMapping("/api/test")
            public String post() {
                return "ok";
            }

            @org.springframework.web.bind.annotation.GetMapping("/api/test")
            public String get() {
                return "ok";
            }
        }
    }
}
