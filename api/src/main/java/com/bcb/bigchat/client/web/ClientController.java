package com.bcb.bigchat.client.web;

import com.bcb.bigchat.billing.application.BalanceAdminService;
import com.bcb.bigchat.client.application.ClientService;
import com.bcb.bigchat.client.domain.Client;
import com.bcb.bigchat.client.domain.PlanType;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;
    private final BalanceAdminService balanceAdminService;

    public ClientController(ClientService clientService, BalanceAdminService balanceAdminService) {
        this.clientService = clientService;
        this.balanceAdminService = balanceAdminService;
    }

    @GetMapping
    public ResponseEntity<List<ClientResponse>> list() {
        return ResponseEntity.ok(clientService.findAll().stream().map(ClientResponse::from).toList());
    }

    @PostMapping
    public ResponseEntity<ClientResponse> create(@Valid @RequestBody CreateClientRequest request) {
        Client client = new Client();
        client.setName(request.name());
        client.setDocumentId(request.documentId());
        client.setDocumentType(request.documentType());
        client.setPlanType(request.planType());
        client.setBalance(request.balance() != null ? request.balance() : BigDecimal.ZERO);
        client.setMonthlyLimit(request.monthlyLimit() != null ? request.monthlyLimit() : BigDecimal.ZERO);
        return ResponseEntity.ok(ClientResponse.from(clientService.create(client)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(ClientResponse.from(clientService.findById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateClientRequest request) {
        Client updates = new Client();
        updates.setName(request.name());
        updates.setPlanType(request.planType());
        updates.setMonthlyLimit(request.monthlyLimit());
        return ResponseEntity.ok(ClientResponse.from(clientService.update(id, updates)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        clientService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/balance")
    public ResponseEntity<Void> adjustBalance(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> body) {
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        String description = (String) body.getOrDefault("description", "Manual adjustment");
        balanceAdminService.adjustBalance(id, amount, description);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/plan")
    public ResponseEntity<Void> changePlan(
            @PathVariable UUID id,
            @RequestBody Map<String, String> body) {
        PlanType newPlan = PlanType.valueOf(body.get("planType"));
        balanceAdminService.changePlan(id, newPlan);
        return ResponseEntity.noContent().build();
    }
}
