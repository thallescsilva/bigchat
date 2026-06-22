package com.bcb.bigchat.billing.application;

import com.bcb.bigchat.billing.domain.Transaction;
import com.bcb.bigchat.billing.domain.TransactionType;
import com.bcb.bigchat.billing.infrastructure.TransactionRepository;
import com.bcb.bigchat.client.domain.Client;
import com.bcb.bigchat.client.domain.PlanType;
import com.bcb.bigchat.client.infrastructure.ClientRepository;
import com.bcb.bigchat.shared.error.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.UUID;

@Service
public class BalanceAdminService {

    private final ClientRepository clientRepository;
    private final TransactionRepository transactionRepository;

    public BalanceAdminService(ClientRepository clientRepository, TransactionRepository transactionRepository) {
        this.clientRepository = clientRepository;
        this.transactionRepository = transactionRepository;
    }

    public void adjustBalance(UUID clientId, BigDecimal amount, String description) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        BigDecimal newBalance = client.getBalance().add(amount);
        client.setBalance(newBalance);
        clientRepository.save(client);
        Transaction tx = new Transaction();
        tx.setClientId(clientId);
        tx.setType(TransactionType.ADJUSTMENT);
        tx.setAmount(amount);
        tx.setBalanceAfter(newBalance);
        tx.setDescription(description);
        transactionRepository.save(tx);
    }

    public void changePlan(UUID clientId, PlanType newPlan) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        if (newPlan == PlanType.PREPAID && client.getMonthlyUsage().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Cannot convert to prepaid with outstanding usage");
        }
        client.setPlanType(newPlan);
        clientRepository.save(client);
    }
}
