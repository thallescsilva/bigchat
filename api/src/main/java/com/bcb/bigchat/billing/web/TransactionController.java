package com.bcb.bigchat.billing.web;

import com.bcb.bigchat.billing.domain.Transaction;
import com.bcb.bigchat.billing.infrastructure.TransactionRepository;
import com.bcb.bigchat.shared.security.AuthenticatedClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/clients")
public class TransactionController {

    private final TransactionRepository transactionRepository;

    public TransactionController(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/{id}/transactions")
    public ResponseEntity<Page<Transaction>> transactions(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedClient auth,
            Pageable pageable) {
        return ResponseEntity.ok(transactionRepository.findByClientIdOrderByCreatedAtDesc(id, pageable));
    }
}
