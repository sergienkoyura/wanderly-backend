create table user_preferences
(
    id             UUID PRIMARY KEY,
    user_id        UUID UNIQUE NOT NULL,
    name           VARCHAR(100),
    travel_type    VARCHAR(20),
    time_per_route INTEGER,
    activity_type  VARCHAR(20),
    city_id        UUID        null
);