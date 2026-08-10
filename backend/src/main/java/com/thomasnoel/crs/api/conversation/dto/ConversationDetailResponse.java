package com.thomasnoel.crs.api.conversation.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.thomasnoel.crs.ai.ModelProvider;

public record ConversationDetailResponse(
        UUID id,
        String title,
        ModelProvider provider,
        String modelId,
        Instant createdAt,
        Instant updatedAt,
        List<MessageResponse> messages
) {
}
