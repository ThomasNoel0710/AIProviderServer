package com.thomasnoel.crs.api.conversation.dto;

import jakarta.validation.constraints.NotBlank;

public record SendMessageRequest(
        @NotBlank String message
) {
}
