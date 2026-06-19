package com.bcb.bigchat.messaging.web;

import com.bcb.bigchat.messaging.domain.MessagePriority;
import com.bcb.bigchat.messaging.domain.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendMessageRequest(
    @NotBlank String recipientId,
    @NotBlank String content,
    @NotNull MessageType type,
    @NotNull MessagePriority priority
) {}
