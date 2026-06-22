package com.bcb.bigchat.messaging.application;

import com.bcb.bigchat.messaging.domain.Message;
import com.bcb.bigchat.messaging.domain.MessagePriority;
import com.bcb.bigchat.messaging.domain.MessageStatus;
import com.bcb.bigchat.messaging.domain.MessageType;
import com.bcb.bigchat.messaging.infrastructure.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InMemoryMessageQueueTest {

    @Mock MessageRepository messageRepository;
    InMemoryMessageQueue queue;

    @BeforeEach
    void setUp() {
        lenient().when(messageRepository.findByStatusOrderByPriorityDescCreatedAtAsc(MessageStatus.QUEUED))
                .thenReturn(List.of());
        queue = new InMemoryMessageQueue(messageRepository);
    }

    private Message makeMessage(UUID clientId, MessagePriority priority, LocalDateTime createdAt) {
        Message m = new Message();
        m.setId(UUID.randomUUID());
        m.setSenderClientId(clientId);
        m.setRecipientId("recipient");
        m.setContent("hello");
        m.setType(MessageType.SMS);
        m.setPriority(priority);
        m.setStatus(MessageStatus.QUEUED);
        m.setCost(priority.getPrice());
        m.setCreatedAt(createdAt);
        return m;
    }

    @Test
    void urgentBeforeNormal() {
        UUID clientId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Message normal = makeMessage(clientId, MessagePriority.NORMAL, now);
        Message urgent = makeMessage(clientId, MessagePriority.URGENT, now.plusSeconds(1));

        queue.enqueue(normal);
        queue.enqueue(urgent);

        List<Message> drained = queue.drainForClient(clientId);
        assertThat(drained).hasSize(2);
        assertThat(drained.get(0).getPriority()).isEqualTo(MessagePriority.URGENT);
        assertThat(drained.get(1).getPriority()).isEqualTo(MessagePriority.NORMAL);
    }

    @Test
    void fifoForSamePriority() {
        UUID clientId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Message first = makeMessage(clientId, MessagePriority.NORMAL, now);
        Message second = makeMessage(clientId, MessagePriority.NORMAL, now.plusSeconds(1));

        queue.enqueue(second);
        queue.enqueue(first);

        List<Message> drained = queue.drainForClient(clientId);
        assertThat(drained.get(0).getCreatedAt()).isEqualTo(first.getCreatedAt());
    }

    @Test
    void drainAllAcrossClients() {
        UUID client1 = UUID.randomUUID();
        UUID client2 = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        queue.enqueue(makeMessage(client1, MessagePriority.NORMAL, now));
        queue.enqueue(makeMessage(client2, MessagePriority.URGENT, now));

        List<Message> all = queue.drainAll();
        assertThat(all).hasSize(2);
        assertThat(all.get(0).getPriority()).isEqualTo(MessagePriority.URGENT);
    }
}
