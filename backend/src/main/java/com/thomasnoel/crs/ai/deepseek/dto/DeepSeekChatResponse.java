package com.thomasnoel.crs.ai.deepseek.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeepSeekChatResponse(
        List<Choice> choices
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Choice(
            DeepSeekMessage message
    ) {
    }
}
