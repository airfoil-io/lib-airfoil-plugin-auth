/*
    The "otp" table contains 6-character or 8-character OTPs (One Time Passwords) for authentication purposes.

    The application layer is responsible for providing a mapping between specific subjects
    (like users) and an OTP recorded in this table.
*/
CREATE TABLE IF NOT EXISTS otp (
    id              UUID,
    active          BOOLEAN     DEFAULT TRUE,
    otp             TEXT        NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    expires_at      TIMESTAMPTZ NOT NULL,

    PRIMARY KEY(id)
);
CREATE INDEX uq_otp ON otp(otp);

/*
    The "update_otp_updated_at" function updates the "updated_at" column.
*/
CREATE FUNCTION update_otp_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ language 'plpgsql';

/*
    The "trigger_update_otp_updated_at" calls the "update_otp_updated_at"
    function whenever a row in the "otp" table is updated.
*/
CREATE TRIGGER trigger_update_otp_updated_at
    BEFORE UPDATE
    ON otp
    FOR EACH ROW
EXECUTE PROCEDURE update_otp_updated_at();
