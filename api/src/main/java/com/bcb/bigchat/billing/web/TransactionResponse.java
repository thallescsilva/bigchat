package com.bcb.bigchat.billing.web;

import com.bcb.bigchat.billing.domain.Transaction;
import com.bcb.bigchat.billing.domain.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(
    UUID id,
    UUID messageId,
    TransactionType type,
    BigDecimal amount,
    BigDecimal balanceAfter,
    String description,
    LocalDateTime createdAt
) {
    public static TransactionResponse from(Transaction t) {
        return new TransactionResponse(
                t.getId(), t.getMessageId(), t.getType(), t.getAmount(),
                t.getBalanceAfter(), t.getDescription(), t.getCreatedAt());
    }
}
