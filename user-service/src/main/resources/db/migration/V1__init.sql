create table user_profile
(
    id          UUID PRIMARY KEY,
    user_id     UUID UNIQUE NOT NULL,
    name        VARCHAR(100),
    avatar_name VARCHAR(30)
);