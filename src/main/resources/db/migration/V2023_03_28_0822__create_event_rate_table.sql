CREATE TABLE IF NOT EXISTS "event_rate"
(
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    event_id   BIGINT                NOT NULL,
    event_type event_type            NOT NULL,
    user_id   BIGINT                NOT NULL,
    rate       SMALLINT              NOT NULL,
    CHECK ( rate <= 5 and rate >= 1 )
);

CREATE UNIQUE INDEX ON "event_rate" (event_id, event_type, user_id);