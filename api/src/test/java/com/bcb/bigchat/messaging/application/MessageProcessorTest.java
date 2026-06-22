package com.bcb.bigchat.messaging.application;

import com.bcb.bigchat.conversation.application.ConversationService;
import com.bcb.bigchat.messaging.domain.Message;
import com.bcb.bigchat.messaging.domain.MessagePriority;
import com.bcb.bigchat.messaging.domain.MessageStatus;
import com.bcb.bigchat.messaging.domain.MessageType;
import com.bcb.bigchat.messaging.infrastructure.MessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageProcessorTest {

    @Mock MessageQueue messageQueue;
    @Mock MessageRepository messageRepository;
    @Mock ConversationService conversationService;
    @InjectMocks MessageProcessor messageProcessor;

    private Message makeMessage(String recipientId) {
        Message m = new Message();
        m.setId(UUID.randomUUID());
        m.setSenderClientId(UUID.randomUUID());
        m.setConversationId(UUID.randomUUID());
        m.setRecipientId(recipientId);
        m.setContent("test");
        m.setType(MessageType.SMS);
        m.setPriority(MessagePriority.NORMAL);
        m.setStatus(MessageStatus.QUEUED);
        m.setCost(BigDecimal.valueOf(0.25));
        m.setCreatedAt(LocalDateTime.now());
        return m;
    }

    @Test
    void invalidRecipientMarkedFailed() {
        Message msg = makeMessage("INVALID_RECIPIENT");
        when(messageQueue.drainForClient(any())).thenReturn(List.of(msg));
        when(messageRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        messageProcessor.drainForClient(msg.getSenderClientId());

        verify(messageRepository, times(2)).save(argThat(m -> {
            Message mm = (Message) m;
            return mm.getStatus() == MessageStatus.PROCESSING || mm.getStatus() == MessageStatus.FAILED;
        }));
    }

    @Test
    void validRecipientMarkedSent() {
        Message msg = makeMessage("valid-recipient");
        when(messageQueue.drainForClient(any())).thenReturn(List.of(msg));
        when(messageRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        messageProcessor.drainForClient(msg.getSenderClientId());

        verify(messageRepository, atLeastOnce()).save(argThat(m -> {
            Message mm = (Message) m;
            return mm.getStatus() == MessageStatus.SENT;
        }));
    }
}
