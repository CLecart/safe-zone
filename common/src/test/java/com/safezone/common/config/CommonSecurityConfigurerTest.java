package com.safezone.common.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

class CommonSecurityConfigurerTest {

    @Test
    void nonMutatingMethods_doNotRequireCsrf() {
        var req = new MockHttpServletRequest();
        req.setMethod("GET");
        assertThat(CommonSecurityConfigurer.shouldProtectCsrf(req)).isFalse();
    }

    @Test
    void mutatingWithAuthorization_noCookies_noCsrf() {
        var req = new MockHttpServletRequest();
        req.setMethod("POST");
        req.addHeader("Authorization", "Bearer token");
        assertThat(CommonSecurityConfigurer.shouldProtectCsrf(req)).isFalse();
    }

    @Test
    void mutatingWithCookies_requiresCsrf() {
        var req = new MockHttpServletRequest();
        req.setMethod("POST");
        req.setCookies(new jakarta.servlet.http.Cookie("JSESSIONID", "abc"));
        assertThat(CommonSecurityConfigurer.shouldProtectCsrf(req)).isTrue();
    }

    @Test
    void mutatingWithoutAuthOrCookies_requiresCsrf() {
        var req = new MockHttpServletRequest();
        req.setMethod("DELETE");
        assertThat(CommonSecurityConfigurer.shouldProtectCsrf(req)).isTrue();
    }
}
