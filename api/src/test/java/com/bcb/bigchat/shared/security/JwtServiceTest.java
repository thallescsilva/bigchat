package com.bcb.bigchat.shared.security;

import com.bcb.bigchat.client.domain.Client;
import com.bcb.bigchat.client.domain.DocumentType;
import com.bcb.bigchat.client.domain.PlanType;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private Client client;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("test-secret-key-at-least-32-characters-long", 3600000L);
        client = new Client();
        client.setId(UUID.randomUUID());
        client.setName("Test");
        client.setDocumentId("52998224725");
        client.setDocumentType(DocumentType.CPF);
        client.setPlanType(PlanType.PREPAID);
        client.setAdmin(false);
    }

    @Test
    void generateAndValidateToken() {
        String token = jwtService.generateToken(client);
        Claims claims = jwtService.validateToken(token);
        assertThat(claims.getSubject()).isEqualTo(client.getId().toString());
    }

    @Test
    void extractClientId() {
        String token = jwtService.generateToken(client);
        assertThat(jwtService.extractClientId(token)).isEqualTo(client.getId());
    }

    @Test
    void isAdminFalse() {
        String token = jwtService.generateToken(client);
        assertThat(jwtService.isAdmin(token)).isFalse();
    }

    @Test
    void isAdminTrue() {
        client.setAdmin(true);
        String token = jwtService.generateToken(client);
        assertThat(jwtService.isAdmin(token)).isTrue();
    }
}
