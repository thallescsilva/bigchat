package com.bcb.bigchat.shared.error;

public class LimitExceededException extends RuntimeException {
    public LimitExceededException(String message) { super(message); }
}
