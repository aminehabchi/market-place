package com.buy01.users.Utils;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.util.Date;

@Component
public class JwtUtils {
    private final long expTime;
    private final RSAPrivateKey rsaPrivateKey;

    public JwtUtils(
            RSAPrivateKey rsaPrivateKey, 
            @Value("${JWT_EXP}") long expTime) {
        this.rsaPrivateKey = rsaPrivateKey;
        this.expTime = expTime;
    }

    public String generateToken(String userName, String role) {
        return Jwts.builder()
                .subject(userName)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expTime))
                .signWith(rsaPrivateKey) 
                .compact();
    }
}