create table bookmarks
(
    id         bigserial     not null,
    title      varchar(500)  not null,
    url        varchar(1024) not null,
    created_at timestamp     not null default now(),
    primary key (id)
);
