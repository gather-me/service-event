CREATE TABLE IF NOT EXISTS "event_invitation"
(
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    event_id   BIGINT      NOT NULL,
    event_type event_type  NOT NULL,
    user_id    BIGINT      NOT NULL,
    date       timestamptz NOT NULL
);

CREATE UNIQUE INDEX ON "event_invitation" (event_id, event_type, user_id);