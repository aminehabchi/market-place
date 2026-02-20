package com.buy01.users.Config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.buy01.users.Utils.JwtUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilterConfig extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserDetailServices userDetailServices;

    public JwtFilterConfig(JwtUtils jwtUtils, UserDetailServices userDetailServices) {
        this.jwtUtils = jwtUtils;
        this.userDetailServices = userDetailServices;
    }

    @Override
    public void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException, UsernameNotFoundException {
        if (isPublicEndpoint(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }
        final String authorizationHeader = request.getHeader("Authorization");
        String authSub = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            // authSub = jwtUtils.extractUsername(token);
        }
        // if (authSub != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        //     UserDetails userdetails = userDetailServices.loadUserByUsername(authSub);
        //     UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
        //             userdetails, null, userdetails.getAuthorities());
        //     usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        //     SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        // }
        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/api/auth/login") ||
                path.startsWith("/api/auth/register") ||
                path.startsWith("/auth/login") ||
                path.startsWith("/auth/register") ||
                path.startsWith("/uploads/");
    }
}
