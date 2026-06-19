package com.bcb.bigchat.shared.error;

public class InactiveClientException extends RuntimeException {
    public InactiveClientException(String message) { super(message); }
}
