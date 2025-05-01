create table user_preferences
(
    id             UUID PRIMARY KEY,
    user_id        UUID UNIQUE NOT NULL,
    name           VARCHAR(100),
    travel_type    VARCHAR(20),
    time_per_route INTEGER,
    activity_type  VARCHAR(20),
    notifications  BOOLEAN,
    geoposition    BOOLEAN,
    health_kit     BOOLEAN,
    city_id        UUID        NOT NULL
);