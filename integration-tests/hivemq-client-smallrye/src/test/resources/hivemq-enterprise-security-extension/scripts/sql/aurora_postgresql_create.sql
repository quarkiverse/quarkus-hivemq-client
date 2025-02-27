-- mqtt client tables
create table users
(
    id                  serial      not null
        constraint users_pkey primary key,
    username            text        not null
        constraint users_username_unique unique,
    password            text,
    password_iterations integer     not null,
    password_salt       text        not null,
    algorithm           varchar(32) not null,
    created_at          timestamptz not null default now(),
    updated_at          timestamptz not null default now()
)
;

create unique index users_id_uindex
    on users (id)
;

create unique index users_username_uindex
    on users (username)
;

comment on column users.password is 'Base64 encoded raw byte array'
;

comment on column users.password_salt is 'Base64 encoded raw byte array'
;

create table roles
(
    id          serial      not null
        constraint roles_pkey primary key,
    name        text        not null
        constraint roles_name_unique unique,
    description text,
    created_at  timestamptz not null default now(),
    updated_at  timestamptz not null default now()
)
;

create unique index roles_id_uindex
    on roles (id)
;

create unique index roles_name_uindex
    on roles (name)
;

create table user_roles
(
    user_id    integer     not null
        constraint user_roles_users_id_fk references users,
    role_id    integer     not null
        constraint user_roles_roles_id_fk references roles,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint user_roles_user_role_pk
        primary key (user_id, role_id)
)
;

create table permissions
(
    id                    serial                not null
        constraint permissions_pkey primary key,
    topic                 text                  not null,
    publish_allowed       boolean default false not null,
    subscribe_allowed     boolean default false not null,
    qos_0_allowed         boolean default false not null,
    qos_1_allowed         boolean default false not null,
    qos_2_allowed         boolean default false not null,
    retained_msgs_allowed boolean default false not null,
    shared_sub_allowed    boolean default false not null,
    shared_group          text,
    created_at            timestamptz           not null default now(),
    updated_at            timestamptz           not null default now()
)
;

create index permissions_topic_index
    on permissions (topic)
;

comment on table permissions is 'All permissions are whitelist permissions'
;

create table role_permissions
(
    role       integer     not null
        constraint role_permissions_roles_id_fk references roles,
    permission integer     not null
        constraint role_permissions_permissions_id_fk references permissions,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint role_permissions_role_permission_pk
        primary key (role, permission)
)
;

create table user_permissions
(
    user_id    integer     not null
        constraint user_permissions_users_id_fk references users,
    permission integer     not null
        constraint user_permissions_permissions_id_fk references permissions,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint user_permissions_user_permission_pk
        primary key (user_id, permission)
)
;

-- control center tables
create table cc_users
(
    id                  serial      not null
        constraint cc_users_pkey primary key,
    username            text        not null
        constraint cc_users_username_unique unique,
    password            text,
    password_iterations integer     not null,
    password_salt       text        not null,
    algorithm           varchar(32) not null,
    created_at          timestamptz not null default now(),
    updated_at          timestamptz not null default now()
)
;

create unique index cc_users_id_uindex
    on cc_users (id)
;

create unique index cc_users_username_uindex
    on cc_users (username)
;

comment on column cc_users.password is 'Base64 encoded raw byte array'
;

comment on column cc_users.password_salt is 'Base64 encoded raw byte array'
;

create table cc_roles
(
    id          serial      not null
        constraint cc_roles_pkey primary key,
    name        text        not null
        constraint cc_roles_name_unique unique,
    description text,
    created_at  timestamptz not null default now(),
    updated_at  timestamptz not null default now()
)
;

create unique index cc_roles_id_uindex
    on cc_roles (id)
;

create unique index cc_roles_name_uindex
    on cc_roles (name)
;

create table cc_user_roles
(
    user_id    integer     not null
        constraint cc_user_roles_users_id_fk references cc_users,
    role_id    integer     not null
        constraint cc_user_roles_roles_id_fk references cc_roles,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint cc_user_roles_user_role_pk
        primary key (user_id, role_id)
)
;

create table cc_permissions
(
    id                serial      not null
        constraint cc_permissions_pkey primary key,
    permission_string text        not null,
    description       text,
    created_at        timestamptz not null default now(),
    updated_at        timestamptz not null default now()
)
;

create table cc_role_permissions
(
    role       integer     not null
        constraint cc_role_permissions_roles_id_fk references cc_roles,
    permission integer     not null
        constraint cc_role_permissions_permissions_id_fk references cc_permissions,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint cc_role_permissions_role_permission_pk
        primary key (role, permission)
)
;

create table cc_user_permissions
(
    user_id    integer     not null
        constraint cc_user_permissions_users_id_fk references cc_users,
    permission integer     not null
        constraint cc_user_permissions_permissions_id_fk references cc_permissions,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint cc_user_permissions_user_permission_pk
        primary key (user_id, permission)
)
;

-- rest api tables
create table rest_api_users
(
    id                  serial      not null
        constraint rest_api_users_pkey primary key,
    username            text        not null
        constraint rest_api_users_username_unique unique,
    password            text,
    password_iterations integer     not null,
    password_salt       text        not null,
    algorithm           varchar(32) not null,
    created_at          timestamptz not null default now(),
    updated_at          timestamptz not null default now()
)
;

create unique index rest_api_users_id_uindex
    on rest_api_users (id)
;

create unique index rest_api_users_username_uindex
    on rest_api_users (username)
;

comment on column rest_api_users.password is 'Base64 encoded raw byte array'
;

comment on column rest_api_users.password_salt is 'Base64 encoded raw byte array'
;

create table rest_api_roles
(
    id          serial      not null
        constraint rest_api_roles_pkey primary key,
    name        text        not null
        constraint rest_api_roles_name_unique unique,
    description text,
    created_at  timestamptz not null default now(),
    updated_at  timestamptz not null default now()
)
;

create unique index rest_api_roles_id_uindex
    on rest_api_roles (id)
;

create unique index rest_api_roles_name_uindex
    on rest_api_roles (name)
;

create table rest_api_user_roles
(
    user_id    integer     not null
        constraint rest_api_user_roles_users_id_fk references rest_api_users,
    role_id    integer     not null
        constraint rest_api_user_roles_roles_id_fk references rest_api_roles,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint rest_api_user_roles_user_role_pk
        primary key (user_id, role_id)
)
;

create table rest_api_permissions
(
    id                serial      not null
        constraint rest_api_permissions_pkey primary key,
    permission_string text        not null,
    description       text,
    created_at        timestamptz not null default now(),
    updated_at        timestamptz not null default now()
)
;

create table rest_api_role_permissions
(
    role       integer     not null
        constraint rest_api_role_permissions_roles_id_fk references rest_api_roles,
    permission integer     not null
        constraint rest_api_role_permissions_permissions_id_fk references rest_api_permissions,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint rest_api_role_permissions_role_permission_pk
        primary key (role, permission)
)
;

create table rest_api_user_permissions
(
    user_id    integer     not null
        constraint rest_api_user_permissions_users_id_fk references rest_api_users,
    permission integer     not null
        constraint rest_api_user_permissions_permissions_id_fk references rest_api_permissions,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint rest_api_user_permissions_user_permission_pk
        primary key (user_id, permission)
)
;

-- functions
create or replace function trigger_set_timestamp()
    returns trigger as
'
    begin
        new.updated_at = now();
        return new;
    end;
' language plpgsql;

-- triggers
-- mqtt triggers
create trigger users_updated_at_trigger
    before update
    on users
    for each row
execute procedure trigger_set_timestamp();

create trigger roles_updated_at_trigger
    before update
    on roles
    for each row
execute procedure trigger_set_timestamp();

create trigger user_roles_updated_at_trigger
    before update
    on user_roles
    for each row
execute procedure trigger_set_timestamp();

create trigger permissions_updated_at_trigger
    before update
    on permissions
    for each row
execute procedure trigger_set_timestamp();

create trigger user_permissions_updated_at_trigger
    before update
    on user_permissions
    for each row
execute procedure trigger_set_timestamp();

create trigger role_permissions_updated_at_trigger
    before update
    on role_permissions
    for each row
execute procedure trigger_set_timestamp();

-- control center triggers
create trigger cc_users_updated_at_trigger
    before update
    on cc_users
    for each row
execute procedure trigger_set_timestamp();

create trigger cc_roles_updated_at_trigger
    before update
    on cc_roles
    for each row
execute procedure trigger_set_timestamp();

create trigger cc_user_roles_updated_at_trigger
    before update
    on cc_user_roles
    for each row
execute procedure trigger_set_timestamp();

create trigger cc_permissions_updated_at_trigger
    before update
    on cc_permissions
    for each row
execute procedure trigger_set_timestamp();

create trigger cc_user_permissions_updated_at_trigger
    before update
    on cc_user_permissions
    for each row
execute procedure trigger_set_timestamp();

create trigger cc_role_permissions_updated_at_trigger
    before update
    on cc_role_permissions
    for each row
execute procedure trigger_set_timestamp();

-- rest api triggers
create trigger rest_api_users_updated_at_trigger
    before update
    on rest_api_users
    for each row
execute procedure trigger_set_timestamp();

create trigger rest_api_roles_updated_at_trigger
    before update
    on rest_api_roles
    for each row
execute procedure trigger_set_timestamp();

create trigger rest_api_user_roles_updated_at_trigger
    before update
    on rest_api_user_roles
    for each row
execute procedure trigger_set_timestamp();

create trigger rest_api_permissions_updated_at_trigger
    before update
    on rest_api_permissions
    for each row
execute procedure trigger_set_timestamp();

create trigger rest_api_user_permissions_updated_at_trigger
    before update
    on rest_api_user_permissions
    for each row
execute procedure trigger_set_timestamp();

create trigger rest_api_role_permissions_updated_at_trigger
    before update
    on cc_role_permissions
    for each row
execute procedure trigger_set_timestamp();
