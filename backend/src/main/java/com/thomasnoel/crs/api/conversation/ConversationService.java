package com.thomasnoel.crs.api.conversation;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.thomasnoel.crs.ai.deepseek.DeepSeekClient;
import com.thomasnoel.crs.ai.deepseek.dto.DeepSeekMessage;
import com.thomasnoel.crs.api.conversation.dto.ConversationDetailResponse;
import com.thomasnoel.crs.api.conversation.dto.ConversationSummaryResponse;
import com.thomasnoel.crs.api.conversation.dto.MessageResponse;
import com.thomasnoel.crs.conversation.ConversationEntity;
import com.thomasnoel.crs.conversation.ConversationStore;
import com.thomasnoel.crs.conversation.MessageEntity;
import org.springframework.stereotype.Service;

@Service
public class ConversationService {

    private final ConversationStore conversationStore;
    private final DeepSeekClient deepSeekClient;

    public ConversationService(
            ConversationStore conversationStore,
            DeepSeekClient deepSeekClient
    ) {
        this.conversationStore = conversationStore;
        this.deepSeekClient = deepSeekClient;
    }

    public ConversationSummaryResponse createConversation() {
        return toSummary(conversationStore.createConversation());
    }

    public List<ConversationSummaryResponse> listConversations() {
        return conversationStore.listConversations().stream()
                .map(this::toSummary)
                .toList();
    }

    public ConversationDetailResponse getConversation(UUID conversationId) {
        ConversationEntity conversation =
                conversationStore.getConversation(conversationId);
        List<MessageResponse> messages =
                conversationStore.getMessages(conversationId).stream()
                        .map(this::toMessageResponse)
                        .toList();

        return new ConversationDetailResponse(
                conversation.getId(),
                conversation.getTitle(),
                conversation.getCreatedAt(),
                conversation.getUpdatedAt(),
                messages
        );
    }

    public void deleteConversation(UUID conversationId) {
        conversationStore.deleteConversation(conversationId);
    }

    public ConversationSummaryResponse renameConversation(
            UUID conversationId,
            String title
    ) {
        return toSummary(
                conversationStore.renameConversation(conversationId, title)
        );
    }

    public MessageResponse sendMessage(
            UUID conversationId,
            String content
    ) {
        List<MessageEntity> history = conversationStore.appendUserMessage(
                conversationId,
                content
        );
        List<DeepSeekMessage> deepSeekMessages = history.stream()
                .map(this::toDeepSeekMessage)
                .toList();

        String answer = deepSeekClient.chat(deepSeekMessages);
        MessageEntity assistantMessage =
                conversationStore.appendAssistantMessage(
                        conversationId,
                        answer
                );

        return toMessageResponse(assistantMessage);
    }

    private ConversationSummaryResponse toSummary(
            ConversationEntity conversation
    ) {
        return new ConversationSummaryResponse(
                conversation.getId(),
                conversation.getTitle(),
                conversation.getCreatedAt(),
                conversation.getUpdatedAt()
        );
    }

    private MessageResponse toMessageResponse(MessageEntity message) {
        return new MessageResponse(
                message.getId(),
                message.getSequenceNumber(),
                message.getRole().name().toLowerCase(Locale.ROOT),
                message.getContent(),
                message.getCreatedAt()
        );
    }

    private DeepSeekMessage toDeepSeekMessage(MessageEntity message) {
        return new DeepSeekMessage(
                message.getRole().name().toLowerCase(Locale.ROOT),
                message.getContent()
        );
    }
}
