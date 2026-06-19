package com.bcb.bigchat.ai.application;

import com.bcb.bigchat.ai.infrastructure.ClaudeClient;
import com.bcb.bigchat.ai.web.AiSuggestionResponse;
import com.bcb.bigchat.messaging.domain.Message;
import com.bcb.bigchat.messaging.domain.MessagePriority;
import com.bcb.bigchat.messaging.domain.MessageStatus;
import com.bcb.bigchat.messaging.domain.MessageType;
import com.bcb.bigchat.messaging.infrastructure.MessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiSuggestionServiceTest {

    @Mock ClaudeClient claudeClient;
    @Mock SuggestionFallback fallbackService;
    @Mock MessageRepository messageRepository;
    ObjectMapper objectMapper = new ObjectMapper();

    AiSuggestionService aiSuggestionService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        aiSuggestionService = new AiSuggestionService(claudeClient, fallbackService, messageRepository, objectMapper);
    }

    private Message makeMessage(UUID convId, UUID senderId) {
        Message m = new Message();
        m.setId(UUID.randomUUID());
        m.setConversationId(convId);
        m.setSenderClientId(senderId);
        m.setRecipientId("recipient");
        m.setContent("hello");
        m.setType(MessageType.SMS);
        m.setPriority(MessagePriority.NORMAL);
        m.setStatus(MessageStatus.SENT);
        m.setCost(BigDecimal.valueOf(0.25));
        m.setCreatedAt(LocalDateTime.now());
        return m;
    }

    @Test
    void configuredClaudeReturnsSuggestions() throws Exception {
        UUID convId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();
        Message msg = makeMessage(convId, clientId);

        when(messageRepository.findByConversationIdOrderByCreatedAtAsc(eq(convId), any()))
                .thenReturn(new PageImpl<>(List.of(msg)));
        when(claudeClient.isConfigured()).thenReturn(true);
        when(claudeClient.sendMessage(anyString(), anyString()))
                .thenReturn("[\"r1\",\"r2\",\"r3\"]");

        AiSuggestionResponse response = aiSuggestionService.suggestReplies(convId, clientId);

        assertThat(response.fallback()).isFalse();
        assertThat(response.suggestions()).containsExactly("r1", "r2", "r3");
    }

    @Test
    void notConfiguredReturnsFallback() {
        UUID convId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        when(messageRepository.findByConversationIdOrderByCreatedAtAsc(eq(convId), any()))
                .thenReturn(new PageImpl<>(List.of()));
        when(claudeClient.isConfigured()).thenReturn(false);
        when(fallbackService.getSuggestions()).thenReturn(List.of("a", "b", "c"));

        AiSuggestionResponse response = aiSuggestionService.suggestReplies(convId, clientId);

        assertThat(response.fallback()).isTrue();
        assertThat(response.suggestions()).containsExactly("a", "b", "c");
    }

    @Test
    void claudeThrowsReturnsFallback() throws Exception {
        UUID convId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        when(messageRepository.findByConversationIdOrderByCreatedAtAsc(eq(convId), any()))
                .thenReturn(new PageImpl<>(List.of()));
        when(claudeClient.isConfigured()).thenReturn(true);
        when(claudeClient.sendMessage(anyString(), anyString())).thenThrow(new RuntimeException("API error"));
        when(fallbackService.getSuggestions()).thenReturn(List.of("x", "y", "z"));

        AiSuggestionResponse response = aiSuggestionService.suggestReplies(convId, clientId);

        assertThat(response.fallback()).isTrue();
    }
}
