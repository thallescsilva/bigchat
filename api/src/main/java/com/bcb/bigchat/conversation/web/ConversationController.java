package com.bcb.bigchat.conversation.web;

import com.bcb.bigchat.conversation.application.ConversationService;
import com.bcb.bigchat.conversation.infrastructure.ConversationRepository;
import com.bcb.bigchat.messaging.infrastructure.MessageRepository;
import com.bcb.bigchat.messaging.web.MessageResponse;
import com.bcb.bigchat.shared.security.AuthenticatedClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/conversations")
public class ConversationController {

    private final ConversationService conversationService;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public ConversationController(ConversationService conversationService,
                                   ConversationRepository conversationRepository,
                                   MessageRepository messageRepository) {
        this.conversationService = conversationService;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    @GetMapping
    public ResponseEntity<List<ConversationResponse>> list(@AuthenticationPrincipal AuthenticatedClient auth) {
        List<ConversationResponse> conversations = conversationService.listForClient(auth.clientId())
                .stream().map(ConversationResponse::from).toList();
        return ResponseEntity.ok(conversations);
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<Page<MessageResponse>> messages(
            @PathVariable java.util.UUID id,
            @AuthenticationPrincipal AuthenticatedClient auth,
            Pageable pageable) {
        return conversationRepository.findByIdAndClientId(id, auth.clientId())
                .map(conv -> ResponseEntity.ok(
                        messageRepository.findByConversationIdOrderByCreatedAtAsc(id, pageable)
                                .map(MessageResponse::from)))
                .orElse(ResponseEntity.status(403).build());
    }
}
