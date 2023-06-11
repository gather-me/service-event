CREATE TYPE "stage_play_category" AS ENUM (
    'Theatre', 'StandUp'
    );

CREATE TABLE IF NOT EXISTS "event_stage_play"
(
    id       BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    category stage_play_category NOT NULL,
    CHECK ( event_type = 'StagePlay' )
) INHERITS (event_base);