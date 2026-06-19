package com.bcb.bigchat.messaging.application;

import com.bcb.bigchat.messaging.domain.Message;
import java.util.List;
import java.util.UUID;

public interface MessageQueue {
    void enqueue(Message message);
    List<Message> drainForClient(UUID clientId);
    List<Message> drainAll();
    int size();
}
