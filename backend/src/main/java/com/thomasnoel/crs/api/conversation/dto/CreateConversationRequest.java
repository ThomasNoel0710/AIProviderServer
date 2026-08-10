package com.thomasnoel.crs.api.conversation.dto;

import com.thomasnoel.crs.ai.ModelProvider;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateConversationRequest(
    @NotNull ModelProvider provider,
    @NotBlank String modelId
) {
}