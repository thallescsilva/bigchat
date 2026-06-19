package com.bcb.bigchat.messaging.application;

import com.bcb.bigchat.messaging.domain.Message;
import com.bcb.bigchat.messaging.domain.MessageStatus;
import com.bcb.bigchat.messaging.infrastructure.MessageRepository;
import com.bcb.bigchat.conversation.application.ConversationService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MessageProcessor {

    private final MessageQueue messageQueue;
    private final MessageRepository messageRepository;
    private final ConversationService conversationService;

    public MessageProcessor(MessageQueue messageQueue, MessageRepository messageRepository,
                            ConversationService conversationService) {
        this.messageQueue = messageQueue;
        this.messageRepository = messageRepository;
        this.conversationService = conversationService;
    }

    public int drainForClient(UUID clientId) {
        List<Message> messages = messageQueue.drainForClient(clientId);
        process(messages);
        return messages.size();
    }

    public int drainAll() {
        List<Message> messages = messageQueue.drainAll();
        process(messages);
        return messages.size();
    }

    private void process(List<Message> messages) {
        for (Message message : messages) {
            message.setStatus(MessageStatus.PROCESSING);
            messageRepository.save(message);

            if ("INVALID_RECIPIENT".equals(message.getRecipientId())) {
                message.setStatus(MessageStatus.FAILED);
            } else {
                message.setStatus(MessageStatus.SENT);
            }
            message.setProcessedAt(LocalDateTime.now());
            messageRepository.save(message);

            conversationService.updateLastMessage(message.getConversationId());
        }
    }
}
