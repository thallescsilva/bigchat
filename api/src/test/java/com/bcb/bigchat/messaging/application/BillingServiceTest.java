package com.bcb.bigchat.messaging.application;

import com.bcb.bigchat.billing.domain.Transaction;
import com.bcb.bigchat.billing.infrastructure.TransactionRepository;
import com.bcb.bigchat.client.domain.Client;
import com.bcb.bigchat.client.domain.PlanType;
import com.bcb.bigchat.client.infrastructure.ClientRepository;
import com.bcb.bigchat.shared.error.InsufficientBalanceException;
import com.bcb.bigchat.shared.error.LimitExceededException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {

    @Mock ClientRepository clientRepository;
    @Mock TransactionRepository transactionRepository;
    @InjectMocks BillingService billingService;

    private Client prepaidClient(BigDecimal balance) {
        Client c = new Client();
        c.setId(UUID.randomUUID());
        c.setPlanType(PlanType.PREPAID);
        c.setBalance(balance);
        c.setMonthlyUsage(BigDecimal.ZERO);
        c.setMonthlyLimit(BigDecimal.ZERO);
        return c;
    }

    private Client postpaidClient(BigDecimal usage, BigDecimal limit) {
        Client c = new Client();
        c.setId(UUID.randomUUID());
        c.setPlanType(PlanType.POSTPAID);
        c.setBalance(BigDecimal.ZERO);
        c.setMonthlyUsage(usage);
        c.setMonthlyLimit(limit);
        return c;
    }

    @Test
    void prepaidDebitSuccess() {
        Client client = prepaidClient(new BigDecimal("10.00"));
        when(clientRepository.save(any())).thenReturn(client);
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        billingService.chargePrepaid(client, new BigDecimal("0.25"));

        assertThat(client.getBalance()).isEqualByComparingTo("9.75");
    }

    @Test
    void prepaidInsufficientBalance() {
        Client client = prepaidClient(new BigDecimal("0.10"));

        assertThatThrownBy(() -> billingService.chargePrepaid(client, new BigDecimal("0.25")))
                .isInstanceOf(InsufficientBalanceException.class);
    }

    @Test
    void postpaidConsumptionSuccess() {
        Client client = postpaidClient(BigDecimal.ZERO, new BigDecimal("100.00"));
        when(clientRepository.save(any())).thenReturn(client);
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        billingService.chargePostpaid(client, new BigDecimal("0.50"));

        assertThat(client.getMonthlyUsage()).isEqualByComparingTo("0.50");
    }

    @Test
    void postpaidLimitExceeded() {
        Client client = postpaidClient(new BigDecimal("99.75"), new BigDecimal("100.00"));

        assertThatThrownBy(() -> billingService.chargePostpaid(client, new BigDecimal("0.50")))
                .isInstanceOf(LimitExceededException.class);
    }
}
