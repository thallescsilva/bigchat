package com.bcb.bigchat.client.web;

import com.bcb.bigchat.billing.application.BalanceAdminService;
import com.bcb.bigchat.client.application.ClientService;
import com.bcb.bigchat.client.domain.Client;
import com.bcb.bigchat.client.domain.PlanType;
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
    public ResponseEntity<List<Client>> list() {
        return ResponseEntity.ok(clientService.findAll());
    }

    @PostMapping
    public ResponseEntity<Client> create(@RequestBody Client client) {
        return ResponseEntity.ok(clientService.create(client));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> get(@PathVariable UUID id) {
        return ResponseEntity.ok(clientService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> update(@PathVariable UUID id, @RequestBody Client updates) {
        return ResponseEntity.ok(clientService.update(id, updates));
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
