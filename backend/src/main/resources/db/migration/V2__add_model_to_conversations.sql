ALTER TABLE conversations
    ADD COLUMN provider VARCHAR(50);

ALTER TABLE conversations
    ADD COLUMN model_id VARCHAR(100);

UPDATE conversations
SET provider = 'DEEPSEEK',
    model_id = 'deepseek-v4-flash';

ALTER TABLE conversations
    ALTER COLUMN provider SET NOT NULL;

ALTER TABLE conversations
    ALTER COLUMN model_id SET NOT NULL;