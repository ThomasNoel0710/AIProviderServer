package com.thomasnoel.crs.conversation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {

    List<MessageEntity> findAllByConversationIdOrderBySequenceNumberAsc(
            UUID conversationId
    );

    Optional<MessageEntity> findFirstByConversationIdOrderBySequenceNumberDesc(
            UUID conversationId
    );
}
