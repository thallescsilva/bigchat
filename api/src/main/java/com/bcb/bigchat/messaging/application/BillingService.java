package com.bcb.bigchat.messaging.application;

import com.bcb.bigchat.billing.domain.Transaction;
import com.bcb.bigchat.billing.domain.TransactionType;
import com.bcb.bigchat.billing.infrastructure.TransactionRepository;
import com.bcb.bigchat.client.domain.Client;
import com.bcb.bigchat.client.domain.PlanType;
import com.bcb.bigchat.client.infrastructure.ClientRepository;
import com.bcb.bigchat.shared.error.InsufficientBalanceException;
import com.bcb.bigchat.shared.error.LimitExceededException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.UUID;

@Service
public class BillingService {

    private final ClientRepository clientRepository;
    private final TransactionRepository transactionRepository;

    public BillingService(ClientRepository clientRepository, TransactionRepository transactionRepository) {
        this.clientRepository = clientRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void chargePrepaid(Client client, BigDecimal cost) {
        if (client.getBalance().compareTo(cost) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }
        BigDecimal newBalance = client.getBalance().subtract(cost);
        client.setBalance(newBalance);
        clientRepository.save(client);
        Transaction tx = new Transaction();
        tx.setClientId(client.getId());
        tx.setType(TransactionType.DEBIT);
        tx.setAmount(cost);
        tx.setBalanceAfter(newBalance);
        tx.setDescription("Message charge");
        transactionRepository.save(tx);
    }

    @Transactional
    public void chargePostpaid(Client client, BigDecimal cost) {
        BigDecimal newUsage = client.getMonthlyUsage().add(cost);
        if (newUsage.compareTo(client.getMonthlyLimit()) > 0) {
            throw new LimitExceededException("Monthly limit exceeded");
        }
        client.setMonthlyUsage(newUsage);
        clientRepository.save(client);
        Transaction tx = new Transaction();
        tx.setClientId(client.getId());
        tx.setType(TransactionType.CONSUMPTION);
        tx.setAmount(cost);
        tx.setBalanceAfter(newUsage);
        tx.setDescription("Message consumption");
        transactionRepository.save(tx);
    }

    public void chargeWithRetry(Client client, BigDecimal cost) {
        int attempts = 0;
        while (true) {
            try {
                if (client.getPlanType() == PlanType.PREPAID) {
                    chargePrepaid(client, cost);
                } else {
                    chargePostpaid(client, cost);
                }
                return;
            } catch (ObjectOptimisticLockingFailureException e) {
                if (++attempts >= 3) throw e;
                client = clientRepository.findById(client.getId()).orElseThrow();
            }
        }
    }
}
