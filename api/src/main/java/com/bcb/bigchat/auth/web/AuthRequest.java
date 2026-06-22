package com.bcb.bigchat.auth.web;

import com.bcb.bigchat.client.domain.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthRequest(
    @NotBlank String documentId,
    @NotNull DocumentType documentType
) {}
