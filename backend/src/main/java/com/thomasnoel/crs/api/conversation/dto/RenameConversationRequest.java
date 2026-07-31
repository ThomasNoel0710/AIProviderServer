package com.thomasnoel.crs.api.conversation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RenameConversationRequest(
        @NotBlank
        @Size(max = 200)
        String title
) {
}
