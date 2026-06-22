package com.bcb.bigchat.messaging.web;

import com.bcb.bigchat.messaging.domain.MessagePriority;
import com.bcb.bigchat.messaging.domain.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SendMessageRequest(
    UUID conversationId,
    String recipientId,
    @NotBlank String content,
    @NotNull MessageType type,
    @NotNull MessagePriority priority
) {}
