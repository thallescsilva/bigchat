package com.bcb.bigchat;

import com.bcb.bigchat.auth.web.AuthRequest;
import com.bcb.bigchat.auth.web.AuthResponse;
import com.bcb.bigchat.client.domain.Client;
import com.bcb.bigchat.client.domain.DocumentType;
import com.bcb.bigchat.client.domain.PlanType;
import com.bcb.bigchat.client.infrastructure.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SecurityHardeningIntegrationTest {

    @Autowired TestRestTemplate restTemplate;
    @Autowired ClientRepository clientRepository;

    private Client clientA;
    private Client clientB;

    @BeforeEach
    void setup() {
        clientRepository.deleteAll();
        clientA = persist("Client A", "52998224725", PlanType.PREPAID);
        clientB = persist("Client B", "11144477735", PlanType.PREPAID);
    }

    private Client persist(String name, String documentId, PlanType plan) {
        Client c = new Client();
        c.setName(name);
        c.setDocumentId(documentId);
        c.setDocumentType(DocumentType.CPF);
        c.setPlanType(plan);
        c.setBalance(new BigDecimal("10.00"));
        c.setActive(true);
        return clientRepository.save(c);
    }

    private HttpHeaders authAs(String documentId) {
        AuthResponse auth = restTemplate.postForObject(
                "/auth", new AuthRequest(documentId, DocumentType.CPF), AuthResponse.class);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(auth.token());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Test
    void createIgnoresAdminEscalation() {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "Hacker");
        body.put("documentId", "12345678909");
        body.put("documentType", "CPF");
        body.put("planType", "PREPAID");
        body.put("admin", true);
        body.put("active", true);

        ResponseEntity<String> resp = restTemplate.exchange(
                "/clients", HttpMethod.POST, new HttpEntity<>(body, authAs("52998224725")), String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        Client created = clientRepository.findByDocumentId("12345678909").orElseThrow();
        assertThat(created.isAdmin()).isFalse();
        assertThat(resp.getBody()).doesNotContain("admin");
    }

    @Test
    void cannotReadAnotherClientsTransactions() {
        ResponseEntity<String> resp = restTemplate.exchange(
                "/clients/" + clientB.getId() + "/transactions",
                HttpMethod.GET, new HttpEntity<>(authAs("52998224725")), String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void canReadOwnTransactions() {
        ResponseEntity<String> resp = restTemplate.exchange(
                "/clients/" + clientA.getId() + "/transactions",
                HttpMethod.GET, new HttpEntity<>(authAs("52998224725")), String.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
