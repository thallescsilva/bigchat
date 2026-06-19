package com.bcb.bigchat.ai.application;

import com.bcb.bigchat.ai.infrastructure.ClaudeClient;
import com.bcb.bigchat.ai.web.AiSuggestionResponse;
import com.bcb.bigchat.messaging.domain.Message;
import com.bcb.bigchat.messaging.infrastructure.MessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class AiSuggestionService {

    private final ClaudeClient claudeClient;
    private final SuggestionFallback fallback;
    private final MessageRepository messageRepository;
    private final ObjectMapper objectMapper;

    public AiSuggestionService(ClaudeClient claudeClient, SuggestionFallback fallback,
                                MessageRepository messageRepository, ObjectMapper objectMapper) {
        this.claudeClient = claudeClient;
        this.fallback = fallback;
        this.messageRepository = messageRepository;
        this.objectMapper = objectMapper;
    }

    public AiSuggestionResponse suggestReplies(UUID conversationId, UUID clientId) {
        try {
            List<Message> messages = messageRepository
                    .findByConversationIdOrderByCreatedAtAsc(conversationId, PageRequest.of(0, 10))
                    .getContent();

            if (!claudeClient.isConfigured()) {
                return new AiSuggestionResponse(fallback.getSuggestions(), true);
            }

            StringBuilder history = new StringBuilder();
            for (Message m : messages) {
                history.append(m.getSenderClientId().equals(clientId) ? "Me: " : "Them: ")
                       .append(m.getContent()).append("\n");
            }

            String systemPrompt = "You are a messaging assistant. Based on the conversation history, suggest 3 short reply options in Portuguese. Return only a JSON array of strings.";
            String userMessage = "Conversation:\n" + history + "\nSuggest 3 replies.";

            String response = claudeClient.sendMessage(systemPrompt, userMessage);
            String jsonPart = response.substring(response.indexOf('['), response.lastIndexOf(']') + 1);
            List<String> suggestions = objectMapper.readValue(jsonPart, List.class);
            return new AiSuggestionResponse(suggestions, false);
        } catch (Exception e) {
            return new AiSuggestionResponse(fallback.getSuggestions(), true);
        }
    }
}
