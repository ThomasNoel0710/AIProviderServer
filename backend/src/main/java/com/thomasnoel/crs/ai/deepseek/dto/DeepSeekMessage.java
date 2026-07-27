package com.thomasnoel.crs.ai.deepseek.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeepSeekMessage(
        String role,
        String content
) {
}
