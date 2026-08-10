package com.thomasnoel.crs.conversation;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.thomasnoel.crs.ai.ModelProvider;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Transactional
class ConversationPersistenceTest {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ConversationStore conversationStore;

    @Autowired
    private EntityManager entityManager;

    @Test
    void savesConversationAndReturnsMessagesInSequence() {
        Instant now = Instant.parse("2026-07-28T12:00:00Z");
        ConversationEntity conversation = conversationRepository.save(
                ConversationEntity.create(
                        "Spring Boot",
                        ModelProvider.DEEPSEEK,
                        "deepseek-v4-flash",
                        now
                )
        );

        messageRepository.save(
                MessageEntity.create(
                        conversation.getId(),
                        2,
                        MessageRole.ASSISTANT,
                        "Spring Boot is a Java application framework.",
                        now.plusSeconds(1)
                )
        );
        messageRepository.save(
                MessageEntity.create(
                        conversation.getId(),
                        1,
                        MessageRole.USER,
                        "What is Spring Boot?",
                        now
                )
        );

        List<MessageEntity> messages =
                messageRepository
                        .findAllByConversationIdOrderBySequenceNumberAsc(
                                conversation.getId()
                        );

        assertEquals(2, messages.size());
        assertEquals(MessageRole.USER, messages.get(0).getRole());
        assertEquals(1, messages.get(0).getSequenceNumber());
        assertEquals(MessageRole.ASSISTANT, messages.get(1).getRole());
        assertEquals(2, messages.get(1).getSequenceNumber());
        assertEquals(ModelProvider.DEEPSEEK, conversation.getProvider());
        assertEquals("deepseek-v4-flash", conversation.getModelId());
    }

    @Test
    void deletingConversationAlsoDeletesItsMessages() {
        Instant now = Instant.parse("2026-07-28T12:00:00Z");
        ConversationEntity conversation = conversationRepository.save(
                ConversationEntity.create(
                        "Delete me",
                        ModelProvider.DEEPSEEK,
                        "deepseek-v4-pro",
                        now
                )
        );
        UUID conversationId = conversation.getId();
        messageRepository.save(
                MessageEntity.create(
                        conversationId,
                        1,
                        MessageRole.USER,
                        "This message should be deleted.",
                        now
                )
        );
        entityManager.flush();

        conversationStore.deleteConversation(conversationId);
        entityManager.flush();
        entityManager.clear();

        assertFalse(conversationRepository.existsById(conversationId));
        assertEquals(
                0,
                messageRepository
                        .findAllByConversationIdOrderBySequenceNumberAsc(
                                conversationId
                        )
                        .size()
        );
    }
}
