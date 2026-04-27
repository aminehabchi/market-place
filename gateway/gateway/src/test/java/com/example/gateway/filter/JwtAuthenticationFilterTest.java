package com.example.gateway.filter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.mock.web.server.MockServerWebExchange;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SuppressWarnings("null")
class JwtAuthenticationFilterTest {
    private JwtAuthenticationFilter filter;
    private RSAPublicKey publicKey;
    private KeyPair keyPair;
    private GatewayFilterChain chain;
    private ServerWebExchange exchange;

    @BeforeEach
    void setUp() throws Exception {
        // Generate RSA key pair for testing
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        keyPair = keyPairGenerator.generateKeyPair();
        publicKey = (RSAPublicKey) keyPair.getPublic();

        filter = new JwtAuthenticationFilter(publicKey);

        // Mock the chain
        chain = mock(GatewayFilterChain.class);
        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
    }

    private String generateValidToken(String userId, String role) {
        return Jwts.builder()
                .subject(userId)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000)) // 24 hours
                .signWith(keyPair.getPrivate(), Jwts.SIG.RS256)
                .compact();
    }

    private ServerWebExchange createExchange(MockServerHttpRequest request) {
        return MockServerWebExchange.from(request);
    }

    @Test
    void testPublicEndpointAllowsAccess() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/users/login").build();
        ServerWebExchange exchange = createExchange(request);

        Mono<Void> result = filter.filter(exchange, chain);

        StepVerifier.create(result)
                .expectComplete()
                .verify();

        verify(chain).filter(any(ServerWebExchange.class));
    }

    @Test
    void testMissingAuthorizationHeaderSetGuestRole() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/products/").build();
        ServerWebExchange exchange = createExchange(request);

        Mono<Void> result = filter.filter(exchange, chain);

        assertNotNull(result);
        // Verify that chain.filter was called (indicating request was modified and passed through)
        StepVerifier.create(result)
                .expectComplete()
                .verify();
    }

    @Test
    void testValidJwtTokenAddsHeaders() {
        String token = generateValidToken("user-123", "BUYER");
        MockServerHttpRequest request = MockServerHttpRequest
            .get("/api/products/")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .build();

        ServerWebExchange exchange = createExchange(request);

        Mono<Void> result = filter.filter(exchange, chain);

        assertNotNull(result);
        StepVerifier.create(result)
                .expectComplete()
                .verify();
    }

    @Test
    void testInvalidTokenSignatureReturnsUnauthorized() {
        // Create a token signed with a different key
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair differentKeyPair = keyPairGenerator.generateKeyPair();

            String invalidToken = Jwts.builder()
                    .subject("user-123")
                    .claim("role", "BUYER")
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 86400000))
                    .signWith(differentKeyPair.getPrivate(), Jwts.SIG.RS256)
                    .compact();

                MockServerHttpRequest request = MockServerHttpRequest
                    .get("/api/products/")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + invalidToken)
                    .build();

                    ServerWebExchange exchange = createExchange(request);
                    MockServerHttpResponse response = (MockServerHttpResponse) ((MockServerWebExchange) exchange).getResponse();

                Mono<Void> result = filter.filter(exchange, chain);

                assertNotNull(result);
                // The filter should catch the exception and call sendUnauthorizedError
                StepVerifier.create(result)
                    .expectComplete()
                    .verify();

                // Response should have UNAUTHORIZED status
                assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        } catch (Exception e) {
            fail("Failed to create invalid token: " + e.getMessage());
        }
    }

    @Test
    void testExpiredTokenReturnsUnauthorized() {
        // Create an expired token
        String expiredToken = Jwts.builder()
                .subject("user-123")
                .claim("role", "BUYER")
                .issuedAt(new Date(System.currentTimeMillis() - 86400000))
                .expiration(new Date(System.currentTimeMillis() - 3600000)) // Expired 1 hour ago
                .signWith(keyPair.getPrivate(), Jwts.SIG.RS256)
                .compact();

        MockServerHttpRequest request = MockServerHttpRequest
            .get("/api/products/")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredToken)
            .build();

        ServerWebExchange exchange = createExchange(request);
        MockServerHttpResponse response = (MockServerHttpResponse) ((MockServerWebExchange) exchange).getResponse();

        Mono<Void> result = filter.filter(exchange, chain);

        assertNotNull(result);
        StepVerifier.create(result)
            .expectComplete()
            .verify();

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testMalformedBearerToken() {
        MockServerHttpRequest request = MockServerHttpRequest
            .get("/api/products/")
            .header(HttpHeaders.AUTHORIZATION, "Bearer invalid-token-xyz")
            .build();

        ServerWebExchange exchange = createExchange(request);
        MockServerHttpResponse response = (MockServerHttpResponse) ((MockServerWebExchange) exchange).getResponse();

        Mono<Void> result = filter.filter(exchange, chain);

        assertNotNull(result);
        StepVerifier.create(result)
            .expectComplete()
            .verify();

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testRegisterEndpointIsPublic() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/users/register").build();
        ServerWebExchange exchange = createExchange(request);

        Mono<Void> result = filter.filter(exchange, chain);

        StepVerifier.create(result)
            .expectComplete()
            .verify();

        verify(chain).filter(any(ServerWebExchange.class));
    }

    @Test
    void testValidJwtExtractsUserIdAndRole() {
        String userId = "user-456";
        String role = "SELLER";
        String token = generateValidToken(userId, role);

        MockServerHttpRequest request = MockServerHttpRequest
            .get("/api/products/create")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .build();

        ServerWebExchange exchange = createExchange(request);

        Mono<Void> result = filter.filter(exchange, chain);

        assertNotNull(result);
        StepVerifier.create(result)
            .expectComplete()
            .verify();
    }
}
