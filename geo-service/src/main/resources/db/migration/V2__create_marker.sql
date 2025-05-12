CREATE TABLE marker
(
    id         UUID PRIMARY KEY,
    city_id    UUID             NOT NULL,
    latitude   DOUBLE PRECISION NOT NULL,
    longitude  DOUBLE PRECISION NOT NULL,
    name       VARCHAR(255)     NOT NULL,
    tag        VARCHAR(20)      NOT NULL, -- e.g. 'park', 'museum', 'statue'
    category   VARCHAR(20)      NOT NULL, -- ENUM: 'NATURE', 'LANDMARK', 'ENTERTAINMENT', 'FOOD', 'SCENIC'
    rating     DOUBLE PRECISION NOT NULL,
    created_at timestamp        not null default current_timestamp,
    foreign key (city_id) references city (id) on delete cascade
);
