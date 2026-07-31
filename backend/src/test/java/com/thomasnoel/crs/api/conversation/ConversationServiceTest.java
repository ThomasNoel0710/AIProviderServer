package com.thomasnoel.crs.api.conversation;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.thomasnoel.crs.ai.deepseek.DeepSeekClient;
import com.thomasnoel.crs.ai.deepseek.dto.DeepSeekMessage;
import com.thomasnoel.crs.api.conversation.dto.MessageResponse;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConversationServiceTest {

    @Mock
    private ConversationStore conversationStore;

    @Mock
    private DeepSeekClient deepSeekClient;

    @InjectMocks
    private ConversationService conversationService;

    @Test
    void deletesConversation() {
        UUID conversationId = UUID.randomUUID();

        conversationService.deleteConversation(conversationId);

        verify(conversationStore).deleteConversation(conversationId);
    }

    @Test
    void sendsTheFullConversationHistoryToDeepSeek() {
        UUID conversationId = UUID.randomUUID();
        Instant now = Instant.parse("2026-07-28T12:00:00Z");
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

        when(
                conversationStore.appendUserMessage(
                        conversationId,
                        "What is my name?"
                )
        ).thenReturn(history);
        when(deepSeekClient.chat(
                org.mockito.ArgumentMatchers.<DeepSeekMessage>anyList()
        )).thenReturn("Your name is Thomas.");
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
        ArgumentCaptor<List<DeepSeekMessage>> messagesCaptor =
                ArgumentCaptor.forClass(List.class);
        verify(deepSeekClient).chat(messagesCaptor.capture());

        List<DeepSeekMessage> sentMessages = messagesCaptor.getValue();
        assertEquals(3, sentMessages.size());
        assertEquals("user", sentMessages.get(0).role());
        assertEquals("My name is Thomas.", sentMessages.get(0).content());
        assertEquals("assistant", sentMessages.get(1).role());
        assertEquals("user", sentMessages.get(2).role());
        assertEquals("Your name is Thomas.", response.content());
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
