create table tags
(
    name varchar(30) not null,
    id   serial
        constraint tags_pk
            primary key
);

create table gift_certificates
(
    name             varchar(30)  not null,
    description      varchar(100) not null,
    price            numeric      not null,
    duration         int          not null,
    create_date      timestamp    not null,
    last_update_date timestamp    not null,
    id               serial
        constraint gift_certificates_pk
            primary key
);

create table certificates_tags
(
    certificate_id integer
        constraint certificates_tags_certificate_id_fkey
            references gift_certificates (id) on delete cascade,
    tag_id         integer
        constraint certificates_tags_tag_id_fkey
            references tags (id) on delete cascade
);