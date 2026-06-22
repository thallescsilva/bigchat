package com.bcb.bigchat.billing.infrastructure;

import com.bcb.bigchat.billing.domain.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Page<Transaction> findByClientIdOrderByCreatedAtDesc(UUID clientId, Pageable pageable);
}
