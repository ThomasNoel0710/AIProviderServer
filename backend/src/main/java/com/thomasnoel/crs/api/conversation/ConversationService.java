package com.thomasnoel.crs.api.conversation;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.thomasnoel.crs.ai.ChatModelClient;
import com.thomasnoel.crs.ai.ChatModelMessage;
import com.thomasnoel.crs.ai.ChatModelRole;
import com.thomasnoel.crs.ai.ModelCatalog;
import com.thomasnoel.crs.ai.UnsupportedModelException;
import com.thomasnoel.crs.ai.ModelProvider;
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
    private final ModelCatalog modelCatalog;
    private final ChatModelClient chatModelClient;

    public ConversationService(
            ConversationStore conversationStore,
            ModelCatalog modelCatalog,
            ChatModelClient chatModelClient
    ) {
        this.conversationStore = conversationStore;
        this.modelCatalog = modelCatalog;
        this.chatModelClient = chatModelClient;
    }

    public ConversationSummaryResponse createConversation(
        ModelProvider provider,
        String modelId
    ) {
        if (!modelCatalog.supports(provider, modelId)) {
            throw new UnsupportedModelException(provider, modelId);
        }
        return toSummary(conversationStore.createConversation(
                provider,
                modelId
        ));
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
                conversation.getProvider(),
                conversation.getModelId(),
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
        ConversationEntity conversation = conversationStore.getConversation(
                conversationId
        );
        List<MessageEntity> history = conversationStore.appendUserMessage(
                conversationId,
                content
        );
        String modelId = conversation.getModelId();
        List<ChatModelMessage> chatModelMessages = history.stream()
                .map(this::toChatModelMessage)
                .toList();

        String answer = chatModelClient.chat(modelId, chatModelMessages);
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
                conversation.getProvider(),
                conversation.getModelId(),
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

    private ChatModelMessage toChatModelMessage(MessageEntity message) {
        return new ChatModelMessage(
                ChatModelRole.valueOf(message.getRole().name()),
                message.getContent()
        );
    }
}
