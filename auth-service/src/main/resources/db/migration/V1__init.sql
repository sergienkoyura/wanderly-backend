create table "user"
(
    id                 uuid primary key,
    email              varchar(255) not null,
    password           varchar(255) not null,
    authorization_type varchar(10)  not null,
    last_logout_at     timestamp    null,
    created_at         timestamp    not null default current_timestamp
)