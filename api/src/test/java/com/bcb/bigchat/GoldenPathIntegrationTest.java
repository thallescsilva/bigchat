package com.bcb.bigchat;

import com.bcb.bigchat.auth.web.AuthRequest;
import com.bcb.bigchat.auth.web.AuthResponse;
import com.bcb.bigchat.client.domain.Client;
import com.bcb.bigchat.client.domain.DocumentType;
import com.bcb.bigchat.client.domain.PlanType;
import com.bcb.bigchat.client.infrastructure.ClientRepository;
import com.bcb.bigchat.conversation.web.ConversationResponse;
import com.bcb.bigchat.messaging.domain.MessagePriority;
import com.bcb.bigchat.messaging.domain.MessageType;
import com.bcb.bigchat.messaging.web.SendMessageRequest;
import com.bcb.bigchat.messaging.web.SendMessageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import java.math.BigDecimal;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class GoldenPathIntegrationTest {

    @Autowired TestRestTemplate restTemplate;
    @Autowired ClientRepository clientRepository;

    private String token;
    private Client savedClient;

    @BeforeEach
    void setup() {
        clientRepository.deleteAll();
        Client client = new Client();
        client.setName("Test User");
        client.setDocumentId("52998224725");
        client.setDocumentType(DocumentType.CPF);
        client.setPlanType(PlanType.PREPAID);
        client.setBalance(new BigDecimal("10.00"));
        client.setActive(true);
        savedClient = clientRepository.save(client);
    }

    @Test
    void goldenPath() {
        AuthRequest authReq = new AuthRequest("52998224725", DocumentType.CPF);
        ResponseEntity<AuthResponse> authResp = restTemplate.postForEntity("/auth", authReq, AuthResponse.class);
        assertThat(authResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        token = authResp.getBody().token();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        SendMessageRequest normalMsg = new SendMessageRequest(null, "recipient-1", "Hello", MessageType.SMS, MessagePriority.NORMAL);
        ResponseEntity<SendMessageResponse> normalResp = restTemplate.exchange(
                "/messages", HttpMethod.POST, new HttpEntity<>(normalMsg, headers), SendMessageResponse.class);
        assertThat(normalResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(normalResp.getBody().cost()).isEqualByComparingTo("0.25");

        SendMessageRequest urgentMsg = new SendMessageRequest(null, "recipient-1", "Urgent!", MessageType.SMS, MessagePriority.URGENT);
        ResponseEntity<SendMessageResponse> urgentResp = restTemplate.exchange(
                "/messages", HttpMethod.POST, new HttpEntity<>(urgentMsg, headers), SendMessageResponse.class);
        assertThat(urgentResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(urgentResp.getBody().cost()).isEqualByComparingTo("0.50");

        ResponseEntity<List<ConversationResponse>> convResp = restTemplate.exchange(
                "/conversations", HttpMethod.GET, new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<ConversationResponse>>() {});
        assertThat(convResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(convResp.getBody()).isNotEmpty();
        assertThat(convResp.getBody().get(0).lastMessageContent()).isEqualTo("Urgent!");

        Client refreshed = clientRepository.findById(savedClient.getId()).orElseThrow();
        assertThat(refreshed.getBalance()).isEqualByComparingTo("9.25");
    }
}
