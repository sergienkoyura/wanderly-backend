create table route
(
    id               UUID PRIMARY KEY,
    city_id          UUID        NOT NULL,
    user_id          UUID        NOT NULL,
    category         VARCHAR(20) NOT NULL,
    avg_time         INTEGER     NOT NULL,
    created_at       timestamp   not null default current_timestamp,
    foreign key (city_id) references city (id) on delete cascade
);

create table route_marker
(
    id           UUID PRIMARY KEY,
    route_id     UUID    NOT NULL,
    marker_id    UUID    NOT NULL,
    order_index  INTEGER NOT NULL DEFAULT 0,
    staying_time INTEGER     NOT NULL,
    foreign key (route_id) references route (id) on delete cascade,
    foreign key (marker_id) references marker (id) on delete cascade
)