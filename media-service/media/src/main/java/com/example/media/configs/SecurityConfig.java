package com.example.media.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.media.filters.HeaderAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
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
                // All GET requests to /api/media/products/** are public
                .requestMatchers(HttpMethod.GET, "/api/media/products/**").permitAll()
                // All GET requests to /api/media/users/** are public
                .requestMatchers(HttpMethod.GET, "/api/media/users/**").permitAll()
                // Non-GET requests to /api/media/products/** require SELLER or ADMIN
                .requestMatchers("/api/media/products/**").hasAnyRole("SELLER", "ADMIN")
                // Non-GET requests to /api/media/users/** require BUYER, SELLER, ADMIN
                .requestMatchers("/api/media/users/**").hasAnyRole("BUYER", "SELLER", "ADMIN")
                // Any other request requires authentication
                .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(
                        headerAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
