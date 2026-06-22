package com.bcb.bigchat.messaging.web;

import com.bcb.bigchat.messaging.application.MessageProcessor;
import com.bcb.bigchat.messaging.application.MessageService;
import com.bcb.bigchat.shared.security.AuthenticatedClient;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;
    private final MessageProcessor messageProcessor;

    public MessageController(MessageService messageService, MessageProcessor messageProcessor) {
        this.messageService = messageService;
        this.messageProcessor = messageProcessor;
    }

    @PostMapping
    public ResponseEntity<SendMessageResponse> send(
            @Valid @RequestBody SendMessageRequest request,
            @AuthenticationPrincipal AuthenticatedClient auth) {
        return ResponseEntity.ok(messageService.send(request, auth));
    }

    @PostMapping("/process")
    public ResponseEntity<?> processAll(@AuthenticationPrincipal AuthenticatedClient auth) {
        if (auth == null || !auth.admin()) {
            return ResponseEntity.status(403).body(Map.of("error", "Admin access required"));
        }
        int processed = messageProcessor.drainAll();
        return ResponseEntity.ok(Map.of("processed", processed));
    }
}
