package com.bcb.bigchat.messaging.application;

import com.bcb.bigchat.client.domain.Client;
import com.bcb.bigchat.client.domain.PlanType;
import com.bcb.bigchat.client.infrastructure.ClientRepository;
import com.bcb.bigchat.conversation.application.ConversationService;
import com.bcb.bigchat.conversation.domain.Conversation;
import com.bcb.bigchat.messaging.domain.Message;
import com.bcb.bigchat.messaging.domain.MessageStatus;
import com.bcb.bigchat.messaging.infrastructure.MessageRepository;
import com.bcb.bigchat.messaging.web.SendMessageRequest;
import com.bcb.bigchat.messaging.web.SendMessageResponse;
import com.bcb.bigchat.shared.error.InactiveClientException;
import com.bcb.bigchat.shared.error.ResourceNotFoundException;
import com.bcb.bigchat.shared.security.AuthenticatedClient;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class MessageService {

    private final ClientRepository clientRepository;
    private final ConversationService conversationService;
    private final MessageRepository messageRepository;
    private final BillingService billingService;
    private final MessageQueue messageQueue;
    private final MessageProcessor messageProcessor;

    public MessageService(ClientRepository clientRepository, ConversationService conversationService,
                          MessageRepository messageRepository, BillingService billingService,
                          MessageQueue messageQueue, MessageProcessor messageProcessor) {
        this.clientRepository = clientRepository;
        this.conversationService = conversationService;
        this.messageRepository = messageRepository;
        this.billingService = billingService;
        this.messageQueue = messageQueue;
        this.messageProcessor = messageProcessor;
    }

    public SendMessageResponse send(SendMessageRequest req, AuthenticatedClient auth) {
        Client client = clientRepository.findById(auth.clientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        if (!client.isActive()) {
            throw new InactiveClientException("Client is inactive");
        }

        Conversation conversation = resolveConversation(req, auth.clientId());

        BigDecimal cost = req.priority().getPrice();
        billingService.chargeWithRetry(client, cost);

        Message message = new Message();
        message.setConversationId(conversation.getId());
        message.setSenderClientId(auth.clientId());
        message.setRecipientId(conversation.getRecipientId());
        message.setContent(req.content());
        message.setType(req.type());
        message.setPriority(req.priority());
        message.setStatus(MessageStatus.QUEUED);
        message.setCost(cost);
        messageRepository.save(message);

        messageQueue.enqueue(message);
        messageProcessor.drainForClient(auth.clientId());

        Client refreshed = clientRepository.findById(auth.clientId()).orElse(client);
        BigDecimal currentBalance = client.getPlanType() == PlanType.PREPAID ? refreshed.getBalance() : null;

        return new SendMessageResponse(
                message.getId(),
                message.getStatus().name().toLowerCase(),
                message.getCreatedAt(),
                message.getProcessedAt(),
                cost,
                currentBalance);
    }

    private Conversation resolveConversation(SendMessageRequest req, java.util.UUID clientId) {
        if (req.conversationId() != null) {
            return conversationService.findOwned(req.conversationId(), clientId);
        }
        if (req.recipientId() != null && !req.recipientId().isBlank()) {
            return conversationService.resolveOrCreate(clientId, req.recipientId());
        }
        throw new IllegalArgumentException("conversationId or recipientId is required");
    }
}
