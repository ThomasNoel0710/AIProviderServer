package com.thomasnoel.crs.api.conversation.dto;

import java.time.Instant;
import java.util.UUID;

import com.thomasnoel.crs.ai.ModelProvider;

public record ConversationSummaryResponse(
        UUID id,
        String title,
        ModelProvider provider,
        String modelId,
        Instant createdAt,
        Instant updatedAt
) {
}
