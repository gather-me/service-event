CREATE TYPE "sport_category" AS ENUM (
    'Football', 'Basketball', 'Volleyball', 'Jogging'
    );

CREATE TABLE IF NOT EXISTS "event_sport"
(
    id       BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    category sport_category NOT NULL,
    CHECK ( event_type = 'Sport' )
) INHERITS (event_base);