create table user_route_completion
(
    id        UUID PRIMARY KEY,
    user_id   UUID         NOT NULL,
    route_id  UUID UNIQUE  NOT NULL,
    status    VARCHAR(30)  NOT NULL,
    step      INTEGER      NOT NULL,
    city_name VARCHAR(100) NOT NULL
);

create table user_ar_model_completion
(
    id        UUID PRIMARY KEY,
    user_id   UUID         NOT NULL,
    model_id  UUID         NOT NULL,
    city_name VARCHAR(100) NOT NULL
);