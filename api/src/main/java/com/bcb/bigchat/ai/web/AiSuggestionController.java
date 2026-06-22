package com.bcb.bigchat.ai.web;

import com.bcb.bigchat.ai.application.AiSuggestionService;
import com.bcb.bigchat.conversation.infrastructure.ConversationRepository;
import com.bcb.bigchat.shared.security.AuthenticatedClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/conversations")
public class AiSuggestionController {

    private final AiSuggestionService aiSuggestionService;
    private final ConversationRepository conversationRepository;

    public AiSuggestionController(AiSuggestionService aiSuggestionService,
                                   ConversationRepository conversationRepository) {
        this.aiSuggestionService = aiSuggestionService;
        this.conversationRepository = conversationRepository;
    }

    @PostMapping("/{id}/ai-suggestions")
    public ResponseEntity<AiSuggestionResponse> suggest(
            @PathVariable UUID id,
            @AuthenticationPrincipal AuthenticatedClient auth) {
        return conversationRepository.findByIdAndClientId(id, auth.clientId())
                .map(conv -> ResponseEntity.ok(aiSuggestionService.suggestReplies(id, auth.clientId())))
                .orElse(ResponseEntity.status(403).build());
    }
}
