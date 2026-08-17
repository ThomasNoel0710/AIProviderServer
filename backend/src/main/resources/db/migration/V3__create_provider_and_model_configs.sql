CREATE TABLE provider_configs (
    id UUID PRIMARY KEY,
    display_name VARCHAR(100) NOT NULL,
    protocol VARCHAR(50) NOT NULL,
    base_url VARCHAR(2048) NOT NULL,
    encrypted_api_key VARCHAR(4096),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE model_configs (
    id UUID PRIMARY KEY,
    provider_config_id UUID NOT NULL,
    model_id VARCHAR(255) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_model_configs_provider_config
        FOREIGN KEY (provider_config_id)
        REFERENCES provider_configs (id),

    CONSTRAINT uk_model_configs_provider_model
        UNIQUE (provider_config_id, model_id)
);
