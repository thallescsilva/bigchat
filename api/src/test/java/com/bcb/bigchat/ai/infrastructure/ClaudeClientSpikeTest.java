package com.bcb.bigchat.ai.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.assertj.core.api.Assertions.assertThat;

@EnabledIfEnvironmentVariable(named = "ANTHROPIC_API_KEY", matches = ".+")
class ClaudeClientSpikeTest {

    @Test
    void sendsMessageToClaude() throws Exception {
        String apiKey = System.getenv("ANTHROPIC_API_KEY");
        ClaudeClient client = new ClaudeClient(apiKey, "https://api.anthropic.com", 10, new ObjectMapper());

        String response = client.sendMessage("You are a helpful assistant.", "Say hello in one word.");

        assertThat(response).isNotBlank();
    }
}
