package com.buy01.users.Utils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
    private final SecretKey key;
    private final long expTime;
    private final JwtParser jwtParser;

    public JwtUtils(@Value("${JWT_SECRET}") String secretKey,
            @Value("${JWT_EXP}") long expTime) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.expTime = expTime;
        this.jwtParser = Jwts.parser()
                .verifyWith(this.key)
                .build();
    }

    public String generateToken(String userName, String role) {
        return Jwts.builder()
                .subject(userName)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expTime))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    private Claims extractClaims(String token) {
        return jwtParser.parseSignedClaims(token).getPayload();
    }

    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }
}