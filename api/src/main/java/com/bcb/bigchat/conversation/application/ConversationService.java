package com.bcb.bigchat.conversation.application;

import com.bcb.bigchat.conversation.domain.Conversation;
import com.bcb.bigchat.conversation.infrastructure.ConversationRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;

    public ConversationService(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    public Conversation resolveOrCreate(UUID clientId, String recipientId) {
        return conversationRepository.findByClientIdAndRecipientId(clientId, recipientId)
                .orElseGet(() -> {
                    Conversation c = new Conversation();
                    c.setClientId(clientId);
                    c.setRecipientId(recipientId);
                    return conversationRepository.save(c);
                });
    }

    public void updateLastMessage(UUID conversationId) {
        conversationRepository.findById(conversationId).ifPresent(c -> {
            c.setLastMessageAt(LocalDateTime.now());
            conversationRepository.save(c);
        });
    }

    public List<Conversation> listForClient(UUID clientId) {
        return conversationRepository.findByClientIdOrderByLastMessageAtDesc(clientId);
    }
}
