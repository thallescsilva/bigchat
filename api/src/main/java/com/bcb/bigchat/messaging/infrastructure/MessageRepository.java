package com.bcb.bigchat.messaging.infrastructure;

import com.bcb.bigchat.messaging.domain.Message;
import com.bcb.bigchat.messaging.domain.MessageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findBySenderClientIdAndStatusOrderByPriorityDescCreatedAtAsc(UUID clientId, MessageStatus status);
    List<Message> findByStatusOrderByPriorityDescCreatedAtAsc(MessageStatus status);
    Page<Message> findByConversationIdOrderByCreatedAtAsc(UUID conversationId, Pageable pageable);
}
