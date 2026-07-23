package com.thomasnoel.crs.ai.deepseek.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DeepSeekChatRequest (
    String model,
    List<DeepSeekMessage> messages,
    Thinking thinking,
    @JsonProperty("max_tokens") int maxTokens,
    boolean stream
)
{    
    public record Thinking (
        String type
    ) {}
}
