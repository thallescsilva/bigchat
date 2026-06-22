package com.bcb.bigchat.messaging.web;

import java.math.BigDecimal;
import java.util.UUID;

public record SendMessageResponse(
    UUID messageId,
    String status,
    BigDecimal cost,
    BigDecimal currentBalance
) {}
