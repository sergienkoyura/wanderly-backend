create table ar_model
(
    id         UUID PRIMARY KEY,
    city_id    UUID             NOT NULL,
    user_id    UUID             NOT NULL,
    latitude   DOUBLE PRECISION NOT NULL,
    longitude  DOUBLE PRECISION NOT NULL,
    code       INT              not null,
    created_at timestamp        not null default current_timestamp,
    foreign key (city_id) references city (id) on delete cascade
)