package com.thomasnoel.crs.ai.deepseek.dto;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeepSeekChatResponseTest {

    private final JsonMapper jsonMapper =
            JsonMapper.builder().build();

    @Test
    void deserializesDeepSeekResponse() throws Exception {
        String responseJson = """
                {
                  "id": "test-response-id",
                  "model": "deepseek-v4-flash",
                  "choices": [
                    {
                      "index": 0,
                      "message": {
                        "role": "assistant",
                        "content": "pong"
                      },
                      "finish_reason": "stop"
                    }
                  ],
                  "usage": {
                    "prompt_tokens": 5,
                    "completion_tokens": 1,
                    "total_tokens": 6
                  }
                }
                """;

        DeepSeekChatResponse response =
                jsonMapper.readValue(
                        responseJson,
                        DeepSeekChatResponse.class
                );

        DeepSeekChatResponse.Choice firstChoice =
                response.choices().get(0);

        assertEquals(
                "assistant",
                firstChoice.message().role()
        );

        assertEquals(
                "pong",
                firstChoice.message().content()
        );
    }
}