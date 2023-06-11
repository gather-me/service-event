CREATE TYPE "event_type" AS ENUM (
    'Musical', 'Sport', 'Nature', 'StagePlay'
    );

CREATE TABLE IF NOT EXISTS "event_base"
(
    id          BIGINT GENERATED ALWAYS AS IDENTITY,
    title       TEXT        NOT NULL,
    description TEXT        NULL,
    creator_id  BIGINT      NOT NULL,
    capacity    BIGINT      NULL,
    enrolled    BIGINT      NOT NULL,
    price       DECIMAL     NULL,
    private     BOOLEAN     NOT NULL,
    start_date  TIMESTAMPTZ NOT NULL,
    end_date    TIMESTAMPTZ NOT NULL,
    location_id BIGINT      NOT NULL,
    event_type  event_type  NOT NULL,
    PRIMARY KEY (id, event_type),
    CHECK ( end_date > event_base.start_date ),
    CHECK ( capacity >= event_base.enrolled ),
    CHECK (enrolled >= 0),
    CHECK (capacity > 0)
);

CREATE UNIQUE INDEX ON "event_base" (id, event_type);