package com.bcb.bigchat.messaging.application;

import com.bcb.bigchat.client.domain.Client;
import com.bcb.bigchat.client.domain.DocumentType;
import com.bcb.bigchat.client.domain.PlanType;
import com.bcb.bigchat.client.infrastructure.ClientRepository;
import com.bcb.bigchat.conversation.application.ConversationService;
import com.bcb.bigchat.conversation.domain.Conversation;
import com.bcb.bigchat.messaging.domain.MessagePriority;
import com.bcb.bigchat.messaging.domain.MessageStatus;
import com.bcb.bigchat.messaging.domain.MessageType;
import com.bcb.bigchat.messaging.infrastructure.MessageRepository;
import com.bcb.bigchat.messaging.web.SendMessageRequest;
import com.bcb.bigchat.messaging.web.SendMessageResponse;
import com.bcb.bigchat.shared.error.InactiveClientException;
import com.bcb.bigchat.shared.security.AuthenticatedClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock ClientRepository clientRepository;
    @Mock ConversationService conversationService;
    @Mock MessageRepository messageRepository;
    @Mock BillingService billingService;
    @Mock MessageQueue messageQueue;
    @Mock MessageProcessor messageProcessor;
    @InjectMocks MessageService messageService;

    @Test
    void inactiveClientThrows() {
        Client client = new Client();
        client.setId(UUID.randomUUID());
        client.setActive(false);
        client.setPlanType(PlanType.PREPAID);
        when(clientRepository.findById(any())).thenReturn(Optional.of(client));

        AuthenticatedClient auth = new AuthenticatedClient(client.getId(), "doc", false);
        SendMessageRequest req = new SendMessageRequest("recipient", "hello", MessageType.SMS, MessagePriority.NORMAL);

        assertThatThrownBy(() -> messageService.send(req, auth))
                .isInstanceOf(InactiveClientException.class);
    }

    @Test
    void sendPrepaidSuccess() {
        UUID clientId = UUID.randomUUID();
        Client client = new Client();
        client.setId(clientId);
        client.setActive(true);
        client.setPlanType(PlanType.PREPAID);
        client.setBalance(new BigDecimal("10.00"));

        Conversation conv = new Conversation();
        conv.setId(UUID.randomUUID());

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(conversationService.resolveOrCreate(any(), any())).thenReturn(conv);
        when(messageRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        AuthenticatedClient auth = new AuthenticatedClient(clientId, "doc", false);
        SendMessageRequest req = new SendMessageRequest("recipient", "hello", MessageType.SMS, MessagePriority.NORMAL);

        SendMessageResponse response = messageService.send(req, auth);

        assertThat(response.cost()).isEqualByComparingTo("0.25");
        assertThat(response.status()).isEqualTo("queued");
        verify(billingService).chargeWithRetry(any(), eq(new BigDecimal("0.25")));
    }

    @Test
    void sendPostpaidSuccess() {
        UUID clientId = UUID.randomUUID();
        Client client = new Client();
        client.setId(clientId);
        client.setActive(true);
        client.setPlanType(PlanType.POSTPAID);
        client.setMonthlyUsage(BigDecimal.ZERO);
        client.setMonthlyLimit(new BigDecimal("100.00"));

        Conversation conv = new Conversation();
        conv.setId(UUID.randomUUID());

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(conversationService.resolveOrCreate(any(), any())).thenReturn(conv);
        when(messageRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        AuthenticatedClient auth = new AuthenticatedClient(clientId, "doc", false);
        SendMessageRequest req = new SendMessageRequest("recipient", "hello", MessageType.SMS, MessagePriority.URGENT);

        SendMessageResponse response = messageService.send(req, auth);

        assertThat(response.cost()).isEqualByComparingTo("0.50");
        assertThat(response.currentBalance()).isNull();
    }
}
