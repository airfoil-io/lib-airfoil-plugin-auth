/*
    The "api_key" table contains 16-byte or 32-byte API keys for authentication purposes.

    The application layer is responsible for providing a mapping between specific subjects
    (like users) and a key recorded in this table.
*/
CREATE TABLE IF NOT EXISTS api_key (
    id              UUID,
    active          BOOLEAN     DEFAULT TRUE,
    api_key         TEXT        NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP(2),

    PRIMARY KEY(id),
    UNIQUE(api_key)
);

CREATE FUNCTION update_api_key_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER trigger_update_api_key_updated_at
    BEFORE UPDATE
    ON api_key
    FOR EACH ROW
EXECUTE PROCEDURE update_api_key_updated_at();
