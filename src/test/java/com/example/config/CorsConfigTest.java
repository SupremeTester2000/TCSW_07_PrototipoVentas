

package com.example.config;

import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.Test;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CorsConfigTest {

    private static class ExposedCorsRegistry extends CorsRegistry {

        Map<String, CorsConfiguration> configurations() {
            return super.getCorsConfigurations();
        }
    }

    @Test
    void configuredOriginIsAllowedAndOtherOriginsAreRejected() {
        ExposedCorsRegistry registry = new ExposedCorsRegistry();

        new CorsConfig("http://localhost:5173")
                .addCorsMappings(registry);

        CorsConfiguration configuration =
                registry.configurations().get("/api/**");

        assertNotNull(configuration);

        assertEquals(
                "http://localhost:5173",
                configuration.checkOrigin("http://localhost:5173"));

        assertNull(
                configuration.checkOrigin("http://untrusted.example"));

        assertTrue(configuration.getAllowedMethods().containsAll(
                Arrays.asList("GET", "POST", "PUT")));

        assertFalse(Boolean.TRUE.equals(
                configuration.getAllowCredentials()));
    }

    @Test
    void emptyOriginConfigurationRejectsCrossOriginRequests() {
        ExposedCorsRegistry registry = new ExposedCorsRegistry();

        new CorsConfig("")
                .addCorsMappings(registry);

        CorsConfiguration configuration =
                registry.configurations().get("/api/**");

        assertNotNull(configuration);

        assertNull(
                configuration.checkOrigin("http://localhost:5173"));
    }
}
