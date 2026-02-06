package com.buy01.users.Config;

import java.io.IOException;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import com.buy01.users.Utils.JwtUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtFilterConfig extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;

    public JwtFilterConfig(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException, UsernameNotFoundException {
        final String authorizationHeader = request.getHeader("Authorization");
        final String authSub;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            if (jwtUtils.extractUsername(token) != null) {
                authSub = jwtUtils.extractUsername(token);
            } else {
                throw new UsernameNotFoundException("user not found");
            }
        }
        filterChain.doFilter(request, response);
    }
}
