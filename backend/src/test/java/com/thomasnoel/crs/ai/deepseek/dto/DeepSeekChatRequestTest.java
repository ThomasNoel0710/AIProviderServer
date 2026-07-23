package com.thomasnoel.crs.ai.deepseek.dto;

import java.util.List;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DeepSeekChatRequestTest {

    private final JsonMapper jsonMapper =
            JsonMapper.builder().build();

    @Test
    void serializesToDeepSeekJsonFormat() throws Exception {
        DeepSeekMessage message =
                new DeepSeekMessage("user", "Hello");

        DeepSeekChatRequest request =
                new DeepSeekChatRequest(
                        "deepseek-v4-flash",
                        List.of(message),
                        new DeepSeekChatRequest.Thinking("disabled"),
                        512,
                        false
                );

        String requestJson =
                jsonMapper.writeValueAsString(request);

        JsonNode json =
                jsonMapper.readTree(requestJson);

        assertEquals(
                "deepseek-v4-flash",
                json.path("model").stringValue()
        );

        assertEquals(
                "user",
                json.path("messages")
                        .get(0)
                        .path("role")
                        .stringValue()
        );

        assertEquals(
                "Hello",
                json.path("messages")
                        .get(0)
                        .path("content")
                        .stringValue()
        );

        assertEquals(
                "disabled",
                json.path("thinking")
                        .path("type")
                        .stringValue()
        );

        assertEquals(
                512,
                json.path("max_tokens").intValue()
        );

        assertFalse(
                json.has("maxTokens"),
                "JSON should use max_tokens, not maxTokens"
        );

        assertFalse(
                json.path("stream").booleanValue()
        );
    }
}