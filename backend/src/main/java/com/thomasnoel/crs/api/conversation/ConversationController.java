package com.thomasnoel.crs.api.conversation;

import java.util.List;
import java.util.UUID;

import com.thomasnoel.crs.api.conversation.dto.ConversationDetailResponse;
import com.thomasnoel.crs.api.conversation.dto.ConversationSummaryResponse;
import com.thomasnoel.crs.api.conversation.dto.MessageResponse;
import com.thomasnoel.crs.api.conversation.dto.RenameConversationRequest;
import com.thomasnoel.crs.api.conversation.dto.SendMessageRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConversationSummaryResponse createConversation() {
        return conversationService.createConversation();
    }

    @GetMapping
    public List<ConversationSummaryResponse> listConversations() {
        return conversationService.listConversations();
    }

    @GetMapping("/{conversationId}")
    public ConversationDetailResponse getConversation(
            @PathVariable UUID conversationId
    ) {
        return conversationService.getConversation(conversationId);
    }

    @DeleteMapping("/{conversationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteConversation(
        @PathVariable UUID conversationId
    ) {
        conversationService.deleteConversation(conversationId);
    }

    @PatchMapping("/{conversationId}")
    public ConversationSummaryResponse renameConversation(
            @PathVariable UUID conversationId,
            @Valid @RequestBody RenameConversationRequest request
    ) {
        return conversationService.renameConversation(
                conversationId,
                request.title()
        );
    }

    @PostMapping("/{conversationId}/messages")
    public MessageResponse sendMessage(
            @PathVariable UUID conversationId,
            @Valid @RequestBody SendMessageRequest request
    ) {
        return conversationService.sendMessage(
                conversationId,
                request.message()
        );
    }
}
