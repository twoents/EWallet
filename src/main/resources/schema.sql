-- Squizzle123!
drop table if exists ew_transaction;
drop table if exists ew_wallet;
drop table if exists ew_session;
drop table if exists ew_user;

create table ew_user (
    id              bigserial           not null,
    username        varchar(128)        not null,
    passhash        varchar(256)        not null,
    primary key ( id ),
    unique ( username )
);

create table ew_session (
    id              bigserial           not null,
    expiry_date     timestamp           not null,
    token           varchar(256)        not null,
    user_id         bigint              not null,
    primary key ( id ),
    foreign key ( user_id ) references ew_user ( id )
);

create table ew_wallet (
    id              bigserial           not null,
    wal_name        varchar(128)        not null,
    balance         numeric(10,2)       not null,
    user_id         bigint              not null,
    primary key ( id ),
    foreign key ( user_id ) references ew_user ( id )
);

create table ew_transaction ( 
    id              bigserial           not null,
    description     varchar(128)        not null,
    tx_date         timestamp           not null,
    amount          numeric(10,2)       not null,
    wallet_id       bigint              not null,
    primary key ( id ),
    foreign key ( wallet_id ) references ew_wallet ( id )
);