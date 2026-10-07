package com.example.gateway.filter;

import java.security.interfaces.RSAPublicKey;
import java.util.Set;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter {

    static final String USER_ID_HEADER = "X-User-Id";
    static final String USER_ROLE_HEADER = "X-User-Role";
    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/api/users/login",
            "/api/users/register",
            "/api/payments/webhooks/stripe");

    private final RSAPublicKey rsaPublicKey;
    private final JwtParser jwtParser;

    public JwtAuthenticationFilter(RSAPublicKey rsaPublicKey) {
        this.rsaPublicKey = rsaPublicKey;
        this.jwtParser = Jwts.parser().verifyWith(rsaPublicKey).build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Identity headers are trusted by downstream services, so they must only
        // ever come from a verified token, never from the client.
        ServerHttpRequest request = exchange.getRequest().mutate()
                .headers(headers -> {
                    headers.remove(USER_ID_HEADER);
                    headers.remove(USER_ROLE_HEADER);
                })
                .build();

        if (isPublicEndpoint(request.getPath().value())) {
            return chain.filter(exchange.mutate().request(request).build());
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header(USER_ROLE_HEADER, "GUEST")
                    .build();
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        }

        String token = authHeader.substring(7);

        try {
            var claims = jwtParser.parseSignedClaims(token).getPayload();
            String userId = claims.getSubject();
            String role = claims.get("role", String.class);
            if (userId == null || userId.isBlank() || role == null || role.isBlank()) {
                return sendUnauthorizedError(exchange.getResponse(), "Invalid token claims");
            }

            ServerHttpRequest modifiedRequest = request.mutate()
                    .header(USER_ID_HEADER, userId)
                    .header(USER_ROLE_HEADER, role)
                    .build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (SignatureException e) {
            return sendUnauthorizedError(exchange.getResponse(), "Invalid token signature");
        } catch (Exception e) {
            return sendUnauthorizedError(exchange.getResponse(), "Invalid or expired token");
        }
    }

    private boolean isPublicEndpoint(String path) {
        String normalized = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
        return PUBLIC_PATHS.contains(normalized);
    }

    // private boolean hasAccess(String path, String role) {
    // if (path.startsWith("/api/products") && "SELLER".equals(role)) {
    // System.out.println("Seller here ======================================");
    // return true;
    // }
    // if (path.startsWith("/api/users/me") && "SELLER".equals(role)) {
    // return true;
    // }
    // if (path.startsWith("/api/seller") && "SELLER".equals(role)) {
    // return true;
    // }
    // return false;
    // }

    private Mono<Void> sendUnauthorizedError(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");
        byte[] body = String.format("{\"error\":\"%s\"}", message).getBytes();
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body)));
    }

    private Mono<Void> sendForbiddenError(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.NOT_FOUND);
        response.getHeaders().add("Content-Type", "application/json");
        byte[] body = String.format("{\"error\":\"%s\"}", message).getBytes();
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body)));
    }
}
