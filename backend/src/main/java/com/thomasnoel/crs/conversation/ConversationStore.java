package com.thomasnoel.crs.conversation;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConversationStore {

    private static final String NEW_CONVERSATION_TITLE = "New conversation";
    private static final int MAX_TITLE_LENGTH = 60;

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public ConversationStore(
            ConversationRepository conversationRepository,
            MessageRepository messageRepository
    ) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    @Transactional
    public ConversationEntity createConversation() {
        return conversationRepository.save(
                ConversationEntity.create(
                        NEW_CONVERSATION_TITLE,
                        Instant.now()
                )
        );
    }

    @Transactional(readOnly = true)
    public List<ConversationEntity> listConversations() {
        return conversationRepository.findAllByOrderByUpdatedAtDesc();
    }

    @Transactional(readOnly = true)
    public ConversationEntity getConversation(UUID conversationId) {
        return findConversation(conversationId);
    }

    @Transactional(readOnly = true)
    public List<MessageEntity> getMessages(UUID conversationId) {
        findConversation(conversationId);
        return messageRepository
                .findAllByConversationIdOrderBySequenceNumberAsc(
                        conversationId
                );
    }

    @Transactional
    public void deleteConversation(UUID conversationId) {
        ConversationEntity conversation = findConversation(conversationId);
        conversationRepository.delete(conversation);
    }

    @Transactional
    public ConversationEntity renameConversation(
            UUID conversationId,
            String title
    ) {
        ConversationEntity conversation = findConversation(conversationId);
        conversation.rename(title.strip(), Instant.now());
        return conversation;
    }

    @Transactional
    public List<MessageEntity> appendUserMessage(
            UUID conversationId,
            String content
    ) {
        ConversationEntity conversation = findConversation(conversationId);
        List<MessageEntity> messages = new ArrayList<>(
                messageRepository
                        .findAllByConversationIdOrderBySequenceNumberAsc(
                                conversationId
                        )
        );
        Instant now = Instant.now();

        MessageEntity userMessage = messageRepository.save(
                MessageEntity.create(
                        conversationId,
                        nextSequenceNumber(messages),
                        MessageRole.USER,
                        content,
                        now
                )
        );

        if (messages.isEmpty()) {
            conversation.rename(createTitle(content), now);
        } else {
            conversation.touch(now);
        }

        messages.add(userMessage);
        return List.copyOf(messages);
    }

    @Transactional
    public MessageEntity appendAssistantMessage(
            UUID conversationId,
            String content
    ) {
        ConversationEntity conversation = findConversation(conversationId);
        long sequenceNumber = messageRepository
                .findFirstByConversationIdOrderBySequenceNumberDesc(
                        conversationId
                )
                .map(message -> message.getSequenceNumber() + 1)
                .orElse(1L);
        Instant now = Instant.now();

        MessageEntity assistantMessage = messageRepository.save(
                MessageEntity.create(
                        conversationId,
                        sequenceNumber,
                        MessageRole.ASSISTANT,
                        content,
                        now
                )
        );
        conversation.touch(now);
        return assistantMessage;
    }

    private ConversationEntity findConversation(UUID conversationId) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(
                        () -> new ConversationNotFoundException(conversationId)
                );
    }

    private long nextSequenceNumber(List<MessageEntity> messages) {
        if (messages.isEmpty()) {
            return 1L;
        }

        return messages.get(messages.size() - 1).getSequenceNumber() + 1;
    }

    private String createTitle(String content) {
        String singleLine = content.strip().replaceAll("\\s+", " ");

        if (singleLine.length() <= MAX_TITLE_LENGTH) {
            return singleLine;
        }

        return singleLine.substring(0, MAX_TITLE_LENGTH - 1) + "…";
    }
}
