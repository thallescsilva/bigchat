package com.bcb.bigchat.conversation.infrastructure;

import com.bcb.bigchat.conversation.domain.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    List<Conversation> findByClientIdOrderByLastMessageAtDesc(UUID clientId);
    Optional<Conversation> findByClientIdAndRecipientId(UUID clientId, String recipientId);
    Optional<Conversation> findByIdAndClientId(UUID id, UUID clientId);
}
