package com.thomasnoel.crs.ai.config;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import com.thomasnoel.crs.ai.ChatProtocol;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "provider_configs")
public class ProviderConfigEntity {
    @Id
    private UUID id;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ChatProtocol protocol;

    @Column(name = "base_url", nullable = false, length = 2048)
    private String baseUrl;

    @Column(name = "encrypted_api_key", length = 4096)
    private String encryptedApiKey;

    @Column(nullable = false)
    private boolean enabled;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ProviderConfigEntity() {
    }

    public ProviderConfigEntity(
        UUID id,
        String displayName,
        ChatProtocol protocol,
        String baseUrl,
        String encryptedApiKey,
        boolean enabled,
        Instant createdAt,
        Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id);
        this.displayName = Objects.requireNonNull(displayName);
        this.protocol = Objects.requireNonNull(protocol);
        this.baseUrl = Objects.requireNonNull(baseUrl);
        this.encryptedApiKey = encryptedApiKey;
        this.enabled = enabled;
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }

    public static ProviderConfigEntity create(
        String displayName,
        ChatProtocol protocol,
        String baseUrl,
        String encryptedApiKey,
        Instant now
    ) {
        return new ProviderConfigEntity(
                UUID.randomUUID(),
                displayName,
                protocol,
                baseUrl,
                encryptedApiKey,
                true,
                now,
                now
        );
    }

    public void updateDetails(
        String displayName,
        String baseUrl,
        String encryptedApiKey,
        Instant now
    ) {
        this.displayName = Objects.requireNonNull(displayName);
        this.baseUrl = Objects.requireNonNull(baseUrl);
        this.encryptedApiKey = encryptedApiKey;
        this.updatedAt = Objects.requireNonNull(now);
    }

    public void enable(Instant now) {
        this.enabled = true;
        this.updatedAt = Objects.requireNonNull(now);
    }

    public void disable(Instant now) {
        this.enabled = false;
        this.updatedAt = Objects.requireNonNull(now);
    }

    
}