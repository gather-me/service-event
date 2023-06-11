CREATE TYPE "musical_category" AS ENUM (
    'Concert', 'Festival'
    );

CREATE TABLE IF NOT EXISTS "event_musical"
(
    id       BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    category musical_category NOT NULL,
    artist   TEXT             NOT NULL,
    CHECK ( event_type = 'Musical' )
) INHERITS (event_base);