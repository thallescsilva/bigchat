package com.bcb.bigchat.messaging.domain;

import java.math.BigDecimal;

public enum MessagePriority {
    NORMAL(new BigDecimal("0.25"), 0),
    URGENT(new BigDecimal("0.50"), 1);

    private final BigDecimal price;
    private final int rank;

    MessagePriority(BigDecimal price, int rank) {
        this.price = price;
        this.rank = rank;
    }

    public BigDecimal getPrice() { return price; }
    public int getRank() { return rank; }
}
