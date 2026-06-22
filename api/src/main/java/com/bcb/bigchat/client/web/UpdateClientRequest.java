package com.bcb.bigchat.client.web;

import com.bcb.bigchat.client.domain.PlanType;
import java.math.BigDecimal;

public record UpdateClientRequest(
    String name,
    PlanType planType,
    BigDecimal monthlyLimit
) {}
