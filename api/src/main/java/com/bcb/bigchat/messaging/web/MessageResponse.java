package com.bcb.bigchat.messaging.web;

import com.bcb.bigchat.messaging.domain.Message;
import com.bcb.bigchat.messaging.domain.MessagePriority;
import com.bcb.bigchat.messaging.domain.MessageStatus;
import com.bcb.bigchat.messaging.domain.MessageType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record MessageResponse(
    UUID id,
    UUID conversationId,
    UUID senderClientId,
    String recipientId,
    String content,
    MessageType type,
    MessagePriority priority,
    MessageStatus status,
    BigDecimal cost,
    LocalDateTime createdAt,
    LocalDateTime processedAt
) {
    public static MessageResponse from(Message m) {
        return new MessageResponse(
                m.getId(), m.getConversationId(), m.getSenderClientId(), m.getRecipientId(),
                m.getContent(), m.getType(), m.getPriority(), m.getStatus(), m.getCost(),
                m.getCreatedAt(), m.getProcessedAt());
    }
}
