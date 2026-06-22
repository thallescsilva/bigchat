package com.bcb.bigchat.messaging.web;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record SendMessageResponse(
    UUID id,
    String status,
    LocalDateTime timestamp,
    LocalDateTime estimatedDelivery,
    BigDecimal cost,
    BigDecimal currentBalance
) {}
