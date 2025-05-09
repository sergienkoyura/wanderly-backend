CREATE TABLE city
(
    id        UUID PRIMARY KEY,
    osm_id    INTEGER          NOT NULL,
    name      VARCHAR(100)     NOT NULL,
    details   VARCHAR(500)     NOT NULL,
    latitude  DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL
);

CREATE TABLE city_bounding_box
(
    city_id      UUID NOT NULL,
    bounding_box DOUBLE PRECISION,
    CONSTRAINT fk_city_bounding_box FOREIGN KEY (city_id) REFERENCES city (id) ON DELETE CASCADE
);

create table user_preferences
(
    id             UUID PRIMARY KEY,
    user_id        UUID UNIQUE NOT NULL,
    city_id        UUID        NOT NULL,
    travel_type    VARCHAR(20),
    time_per_route INTEGER,
    activity_type  VARCHAR(20),
    foreign key (city_id) references city (id) on delete restrict
);