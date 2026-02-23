package com.example.products.config;

import com.example.products.filters.HeaderAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final HeaderAuthenticationFilter headerAuthenticationFilter;

    public SecurityConfig(HeaderAuthenticationFilter headerAuthenticationFilter) {
        this.headerAuthenticationFilter = headerAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Public product listing (GET /api/products)
                .requestMatchers(HttpMethod.GET, "/api/products/").hasAnyRole("GUEST", "BUYER", "SELLER", "ADMIN")

                // Actuator only for admin
                .requestMatchers("/actuator/**").hasRole("ADMIN")

                // Everything else requires authenticated user
                .anyRequest().hasAnyRole("BUYER", "SELLER", "ADMIN")
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // ✅ Add your custom header filter BEFORE Spring's auth filter
            .addFilterBefore(
                headerAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}