package com.bcb.bigchat.client.web;

import com.bcb.bigchat.client.domain.Client;
import com.bcb.bigchat.client.domain.DocumentType;
import com.bcb.bigchat.client.domain.PlanType;
import java.math.BigDecimal;
import java.util.UUID;

public record ClientResponse(
    UUID id,
    String name,
    String documentId,
    DocumentType documentType,
    PlanType planType,
    BigDecimal balance,
    BigDecimal monthlyLimit,
    BigDecimal monthlyUsage,
    boolean active
) {
    public static ClientResponse from(Client c) {
        return new ClientResponse(
                c.getId(), c.getName(), c.getDocumentId(), c.getDocumentType(),
                c.getPlanType(), c.getBalance(), c.getMonthlyLimit(), c.getMonthlyUsage(), c.isActive());
    }
}
