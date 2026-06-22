package com.bcb.bigchat.billing.application;

import com.bcb.bigchat.billing.infrastructure.TransactionRepository;
import com.bcb.bigchat.client.domain.Client;
import com.bcb.bigchat.client.domain.PlanType;
import com.bcb.bigchat.client.infrastructure.ClientRepository;
import com.bcb.bigchat.shared.error.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalanceAdminServiceTest {

    @Mock ClientRepository clientRepository;
    @Mock TransactionRepository transactionRepository;
    @InjectMocks BalanceAdminService balanceAdminService;

    @Test
    void adjustsBalance() {
        Client client = new Client();
        client.setId(UUID.randomUUID());
        client.setBalance(new BigDecimal("10.00"));
        when(clientRepository.findById(client.getId())).thenReturn(Optional.of(client));
        when(clientRepository.save(any())).thenReturn(client);
        when(transactionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        balanceAdminService.adjustBalance(client.getId(), new BigDecimal("5.00"), "Credit");

        assertThat(client.getBalance()).isEqualByComparingTo("15.00");
    }

    @Test
    void changePlanPostpaidToPrepaidWithZeroUsage() {
        Client client = new Client();
        client.setId(UUID.randomUUID());
        client.setPlanType(PlanType.POSTPAID);
        client.setMonthlyUsage(BigDecimal.ZERO);
        when(clientRepository.findById(client.getId())).thenReturn(Optional.of(client));
        when(clientRepository.save(any())).thenReturn(client);

        balanceAdminService.changePlan(client.getId(), PlanType.PREPAID);

        assertThat(client.getPlanType()).isEqualTo(PlanType.PREPAID);
    }

    @Test
    void changePlanBlockedWithOutstandingUsage() {
        Client client = new Client();
        client.setId(UUID.randomUUID());
        client.setPlanType(PlanType.POSTPAID);
        client.setMonthlyUsage(new BigDecimal("5.00"));
        when(clientRepository.findById(client.getId())).thenReturn(Optional.of(client));

        assertThatThrownBy(() -> balanceAdminService.changePlan(client.getId(), PlanType.PREPAID))
                .isInstanceOf(IllegalStateException.class);
    }
}
