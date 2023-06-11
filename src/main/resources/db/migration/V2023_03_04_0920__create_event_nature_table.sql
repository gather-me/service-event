CREATE TYPE "nature_category" AS ENUM (
    'Camp', 'Hiking'
    );

CREATE TABLE IF NOT EXISTS "event_nature"
(
    id       BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    category nature_category NOT NULL,
    CHECK ( event_type = 'Nature' )
) INHERITS (event_base);