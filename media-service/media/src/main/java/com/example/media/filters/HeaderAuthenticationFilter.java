package com.example.media.filters;

import java.io.IOException;
import java.util.List;

import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.shared.common.types.Role;
import com.mongodb.lang.NonNull;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Order(1)
@Component
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String userId = request.getHeader("X-User-Id");
        String roleHeader = request.getHeader("X-User-Role");

        System.out.println("----> " + request.getRequestURL().toString());
        System.out.println("userId ========> " + userId);
        System.out.println("role ========> " + roleHeader);

        if (roleHeader == null || roleHeader.isBlank()) {
            // No role header, just continue
            filterChain.doFilter(request, response);
            return;
        }
        System.out.println(" ---- ================ " + roleHeader);
        Role role = Role.fromString(roleHeader);
        System.out.println(" ++++ ================ " + role);

        // Only parse UUID for roles that need it
        if (role.isBuyer() || role.isSeller() || role.isAdmin()) {
            if (userId == null || userId.isBlank()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        if (role.isGuest()) {
            userId = "";
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            var authorities = List.of(new SimpleGrantedAuthority(roleHeader));

            var authentication = new UsernamePasswordAuthenticationToken(
                    userId, // null for guest
                    null,
                    authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        System.out.println("Authorities: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        filterChain.doFilter(request, response);
    }
}