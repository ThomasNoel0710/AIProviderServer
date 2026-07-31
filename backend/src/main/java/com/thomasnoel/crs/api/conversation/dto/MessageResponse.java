package com.thomasnoel.crs.api.conversation.dto;

import java.time.Instant;
import java.util.UUID;

public record MessageResponse(
        UUID id,
        long sequenceNumber,
        String role,
        String content,
        Instant createdAt
) {
}
