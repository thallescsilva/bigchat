package com.bcb.bigchat.shared.security;

import com.bcb.bigchat.client.domain.Client;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(Client client) {
        return Jwts.builder()
                .subject(client.getId().toString())
                .claim("documentId", client.getDocumentId())
                .claim("admin", client.isAdmin())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public UUID extractClientId(String token) {
        return UUID.fromString(validateToken(token).getSubject());
    }

    public boolean isAdmin(String token) {
        Claims claims = validateToken(token);
        return Boolean.TRUE.equals(claims.get("admin", Boolean.class));
    }
}
