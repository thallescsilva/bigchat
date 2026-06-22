package com.bcb.bigchat.ai.application;

import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class SuggestionFallback {
    public List<String> getSuggestions() {
        return List.of(
            "Entendido, vou verificar isso.",
            "Obrigado pela mensagem!",
            "Pode me dar mais detalhes?"
        );
    }
}
