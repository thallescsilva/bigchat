package com.bcb.bigchat.auth.web;

import com.bcb.bigchat.client.domain.DocumentType;
import com.bcb.bigchat.client.domain.PlanType;
import java.util.UUID;

public record ClientSummary(
    UUID id,
    String name,
    String documentId,
    DocumentType documentType,
    PlanType planType,
    boolean active
) {}
