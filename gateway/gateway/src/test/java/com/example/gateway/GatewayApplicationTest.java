package com.example.gateway;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

class GatewayApplicationTest {

    @Test
    void gatewayApplicationHasExpectedBootAnnotations() {
        assertNotNull(GatewayApplication.class.getAnnotation(SpringBootApplication.class));
        assertNotNull(GatewayApplication.class.getAnnotation(EnableDiscoveryClient.class));
    }

    @Test
    void mainMethodExists() throws NoSuchMethodException {
        assertTrue(GatewayApplication.class.getMethod("main", String[].class) != null);
    }
}
