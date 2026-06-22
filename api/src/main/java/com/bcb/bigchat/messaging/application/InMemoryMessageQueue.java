package com.bcb.bigchat.messaging.application;

import com.bcb.bigchat.messaging.domain.Message;
import com.bcb.bigchat.messaging.infrastructure.MessageRepository;
import com.bcb.bigchat.messaging.domain.MessageStatus;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

@Component
public class InMemoryMessageQueue implements MessageQueue, ApplicationRunner {

    private final ConcurrentHashMap<UUID, PriorityBlockingQueue<Message>> queues = new ConcurrentHashMap<>();
    private final MessageRepository messageRepository;

    private static final Comparator<Message> MESSAGE_COMPARATOR = Comparator
            .comparingInt((Message m) -> m.getPriority().getRank())
            .reversed()
            .thenComparing(Message::getCreatedAt)
            .thenComparing(m -> m.getId() == null ? "" : m.getId().toString());

    public InMemoryMessageQueue(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    public void enqueue(Message message) {
        queues.computeIfAbsent(message.getSenderClientId(),
                k -> new PriorityBlockingQueue<>(11, MESSAGE_COMPARATOR)).add(message);
    }

    @Override
    public List<Message> drainForClient(UUID clientId) {
        PriorityBlockingQueue<Message> queue = queues.get(clientId);
        if (queue == null) return List.of();
        List<Message> result = new ArrayList<>();
        queue.drainTo(result);
        result.sort(MESSAGE_COMPARATOR);
        return result;
    }

    @Override
    public List<Message> drainAll() {
        List<Message> all = new ArrayList<>();
        for (UUID clientId : queues.keySet()) {
            all.addAll(drainForClient(clientId));
        }
        all.sort(MESSAGE_COMPARATOR);
        return all;
    }

    @Override
    public int size() {
        return queues.values().stream().mapToInt(PriorityBlockingQueue::size).sum();
    }

    @Override
    public void run(ApplicationArguments args) {
        List<Message> queued = messageRepository.findByStatusOrderByPriorityDescCreatedAtAsc(MessageStatus.QUEUED);
        queued.forEach(this::enqueue);
    }
}
