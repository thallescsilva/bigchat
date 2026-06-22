package com.bcb.bigchat.client.web;

import com.bcb.bigchat.client.domain.DocumentType;
import com.bcb.bigchat.client.domain.PlanType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateClientRequest(
    @NotBlank String name,
    @NotBlank String documentId,
    @NotNull DocumentType documentType,
    @NotNull PlanType planType,
    BigDecimal balance,
    BigDecimal monthlyLimit
) {}
