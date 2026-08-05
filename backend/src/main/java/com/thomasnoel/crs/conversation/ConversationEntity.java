package com.thomasnoel.crs.conversation;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.thomasnoel.crs.ai.ModelProvider;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
@Table(name = "conversations")
public class ConversationEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ModelProvider provider;
    @Column(name = "model_id", nullable = false, length = 100)
    private String modelId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ConversationEntity() {
    }

    public ConversationEntity(
            UUID id,
            String title,
            ModelProvider provider,
            String modelId,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id);
        this.title = Objects.requireNonNull(title);
        this.provider = Objects.requireNonNull(provider);
        this.modelId = Objects.requireNonNull(modelId);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }

    public static ConversationEntity create(String title, 
        ModelProvider provider, 
        String modelId, 
        Instant now) {
        return new ConversationEntity(
                UUID.randomUUID(),
                title,
                provider,
                modelId,
                now,
                now
        );
    }

    public void rename(String title, Instant now) {
        this.title = Objects.requireNonNull(title);
        this.updatedAt = Objects.requireNonNull(now);
    }

    public void touch(Instant now) {
        this.updatedAt = Objects.requireNonNull(now);
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public ModelProvider getProvider() {
        return provider;
    }

    public String getModelId() {
        return modelId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
