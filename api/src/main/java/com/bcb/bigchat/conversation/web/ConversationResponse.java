package com.bcb.bigchat.conversation.web;

import com.bcb.bigchat.conversation.domain.Conversation;
import java.time.LocalDateTime;
import java.util.UUID;

public record ConversationResponse(
    UUID id,
    String recipientId,
    String recipientName,
    String lastMessageContent,
    LocalDateTime lastMessageTime,
    int unreadCount
) {
    public static ConversationResponse from(Conversation c) {
        return new ConversationResponse(
                c.getId(),
                c.getRecipientId(),
                c.getRecipientId(),
                c.getLastMessageContent(),
                c.getLastMessageAt(),
                c.getUnreadCount());
    }
}
