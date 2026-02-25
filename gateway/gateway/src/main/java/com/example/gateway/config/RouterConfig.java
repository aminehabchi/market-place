package com.example.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouterConfig {
    @Bean
    public RouteLocator routerBuilder(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("products", r -> r
                        .path("/api/products/**")
                        .uri("lb://products"))
                .route("users", r -> r
                        .path("/api/users/**")
                        .uri("lb://users"))
                .route("media", r -> r
                        .path("/api/media/**")
                        .uri("lb://media"))
                .build();
    }
}
