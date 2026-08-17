package com.thomasnoel.crs.api.conversation;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.thomasnoel.crs.ai.ChatModelClient;
import com.thomasnoel.crs.ai.ChatModelClientRouter;
import com.thomasnoel.crs.ai.ChatModelDefinition;
import com.thomasnoel.crs.ai.ChatModelMessage;
import com.thomasnoel.crs.ai.ChatModelRole;
import com.thomasnoel.crs.ai.ChatProtocol;
import com.thomasnoel.crs.ai.ModelCatalog;
import com.thomasnoel.crs.ai.ModelProvider;
import com.thomasnoel.crs.ai.UnsupportedModelException;
import com.thomasnoel.crs.api.conversation.dto.MessageResponse;
import com.thomasnoel.crs.conversation.ConversationEntity;
import com.thomasnoel.crs.conversation.ConversationStore;
import com.thomasnoel.crs.conversation.MessageEntity;
import com.thomasnoel.crs.conversation.MessageRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConversationServiceTest {

    @Mock
    private ConversationStore conversationStore;

    @Mock
    private ModelCatalog modelCatalog;

    @Mock
    private ChatModelClient chatModelClient;

    @Mock
    private ChatModelClientRouter chatModelClientRouter;

    @InjectMocks
    private ConversationService conversationService;

    @Test
    void deletesConversation() {
        UUID conversationId = UUID.randomUUID();

        conversationService.deleteConversation(conversationId);

        verify(conversationStore).deleteConversation(conversationId);
    }

    @Test
    void createsConversationWithSupportedModel() {
        Instant now = Instant.parse("2026-07-28T12:00:00Z");
        ConversationEntity conversation = conversation(
                UUID.randomUUID(),
                now
        );
        when(
                modelCatalog.supports(
                        ModelProvider.DEEPSEEK,
                        "deepseek-v4-flash"
                )
        ).thenReturn(true);
        when(
                conversationStore.createConversation(
                        ModelProvider.DEEPSEEK,
                        "deepseek-v4-flash"
                )
        ).thenReturn(conversation);

        var response = conversationService.createConversation(
                ModelProvider.DEEPSEEK,
                "deepseek-v4-flash"
        );

        assertEquals(ModelProvider.DEEPSEEK, response.provider());
        assertEquals("deepseek-v4-flash", response.modelId());
    }

    @Test
    void rejectsUnsupportedModel() {
        when(
                modelCatalog.supports(
                        ModelProvider.OPENAI,
                        "unknown"
                )
        ).thenReturn(false);

        assertThrows(
                UnsupportedModelException.class,
                () -> conversationService.createConversation(
                        ModelProvider.OPENAI,
                        "unknown"
                )
        );
        verify(conversationStore, never()).createConversation(
                ModelProvider.OPENAI,
                "unknown"
        );
    }

    @Test
    void sendsTheFullConversationHistoryToSelectedModel() {
        UUID conversationId = UUID.randomUUID();
        Instant now = Instant.parse("2026-07-28T12:00:00Z");
        ConversationEntity conversation = conversation(conversationId, now);
        List<MessageEntity> history = List.of(
                message(
                        conversationId,
                        1,
                        MessageRole.USER,
                        "My name is Thomas.",
                        now
                ),
                message(
                        conversationId,
                        2,
                        MessageRole.ASSISTANT,
                        "Nice to meet you, Thomas.",
                        now.plusSeconds(1)
                ),
                message(
                        conversationId,
                        3,
                        MessageRole.USER,
                        "What is my name?",
                        now.plusSeconds(2)
                )
        );
        MessageEntity assistantMessage = message(
                conversationId,
                4,
                MessageRole.ASSISTANT,
                "Your name is Thomas.",
                now.plusSeconds(3)
        );
        ChatModelDefinition modelDefinition = new ChatModelDefinition(
                ModelProvider.DEEPSEEK,
                ChatProtocol.OPENAI_CHAT_COMPLETIONS,
                "deepseek-v4-flash",
                "DeepSeek-V4-Flash"
        );

        when(conversationStore.getConversation(conversationId))
                .thenReturn(conversation);
        when(
                modelCatalog.getModel(
                        ModelProvider.DEEPSEEK,
                        "deepseek-v4-flash"
                )
        ).thenReturn(modelDefinition);
        when(
                chatModelClientRouter.getClient(
                        ChatProtocol.OPENAI_CHAT_COMPLETIONS
                )
        ).thenReturn(chatModelClient);
        when(
                conversationStore.appendUserMessage(
                        conversationId,
                        "What is my name?"
                )
        ).thenReturn(history);
        when(
                chatModelClient.chat(
                        eq("deepseek-v4-flash"),
                        anyList()
                )
        ).thenReturn("Your name is Thomas.");
        when(
                conversationStore.appendAssistantMessage(
                        conversationId,
                        "Your name is Thomas."
                )
        ).thenReturn(assistantMessage);

        MessageResponse response = conversationService.sendMessage(
                conversationId,
                "What is my name?"
        );

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ChatModelMessage>> messagesCaptor =
                ArgumentCaptor.forClass(List.class);
        verify(chatModelClient).chat(
                eq("deepseek-v4-flash"),
                messagesCaptor.capture()
        );

        List<ChatModelMessage> sentMessages = messagesCaptor.getValue();
        assertEquals(3, sentMessages.size());
        assertEquals(ChatModelRole.USER, sentMessages.get(0).role());
        assertEquals("My name is Thomas.", sentMessages.get(0).content());
        assertEquals(ChatModelRole.ASSISTANT, sentMessages.get(1).role());
        assertEquals(ChatModelRole.USER, sentMessages.get(2).role());
        assertEquals("Your name is Thomas.", response.content());
    }

    private ConversationEntity conversation(UUID id, Instant now) {
        return new ConversationEntity(
                id,
                "New conversation",
                ModelProvider.DEEPSEEK,
                "deepseek-v4-flash",
                now,
                now
        );
    }

    private MessageEntity message(
            UUID conversationId,
            long sequenceNumber,
            MessageRole role,
            String content,
            Instant createdAt
    ) {
        return new MessageEntity(
                UUID.randomUUID(),
                conversationId,
                sequenceNumber,
                role,
                content,
                createdAt
        );
    }
}
