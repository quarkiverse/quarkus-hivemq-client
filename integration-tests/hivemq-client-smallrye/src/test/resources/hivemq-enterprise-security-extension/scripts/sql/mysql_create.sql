-- mqtt clients
create table users
(
    id                  integer      not null auto_increment unique,
    username            varchar(762) not null unique,
    password            text comment 'Base64 encoded raw byte array',
    password_iterations integer      not null,
    password_salt       text         not null comment 'Base64 encoded raw byte array',
    algorithm           varchar(32)  not null,
    created_at          timestamp    not null default now(),
    updated_at          timestamp    not null default now() on update now(),
    constraint users_pkey primary key (id)
)
;

create unique index users_id_uindex
    on users (id)
;

create unique index users_username_uindex
    on users (username)
;

create table roles
(
    id          integer      not null auto_increment unique,
    name        varchar(762) not null unique,
    description text,
    created_at  timestamp    not null default now(),
    updated_at  timestamp    not null default now() on update now(),
    constraint roles_pkey primary key (id)
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
    user_id    integer   not null,
    role_id    integer   not null,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now() on update now(),
    constraint user_roles_user_role_pk primary key (user_id, role_id),
    constraint user_roles_users_id_fk foreign key (user_id) references users (id),
    constraint user_roles_roles_id_fk foreign key (role_id) references roles (id)
)
;

create table permissions
(
    id                    integer               not null auto_increment unique,
    topic                 text                  not null,
    publish_allowed       boolean default false not null,
    subscribe_allowed     boolean default false not null,
    qos_0_allowed         boolean default false not null,
    qos_1_allowed         boolean default false not null,
    qos_2_allowed         boolean default false not null,
    retained_msgs_allowed boolean default false not null,
    shared_sub_allowed    boolean default false not null,
    shared_group          text,
    created_at            timestamp             not null default now(),
    updated_at            timestamp             not null default now() on update now(),
    constraint permissions_pkey primary key (id)
)
comment
='All permissions are whitelist permissions'
;

create index permissions_topic_index
    on permissions (topic(767))
;

create table role_permissions
(
    role       integer   not null,
    permission integer   not null,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now() on update now(),
    constraint role_permissions_role_permission_pk primary key (role, permission),
    constraint role_permissions_roles_id_fk foreign key (role) references roles (id),
    constraint role_permissions_permissions_id_fk foreign key (permission) references permissions (id)
)
;

create table user_permissions
(
    user_id    integer   not null,
    permission integer   not null,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now() on update now(),
    constraint user_permissions_user_permission_pk primary key (user_id, permission),
    constraint user_permissions_users_id_fk foreign key (user_id) references users (id),
    constraint user_permissions_permissions_id_fk foreign key (permission) references permissions (id)
)
;

-- control center tables
create table cc_users
(
    id                  integer      not null auto_increment unique,
    username            varchar(762) not null unique,
    password            text comment 'Base64 encoded raw byte array',
    password_iterations integer      not null,
    password_salt       text         not null comment 'Base64 encoded raw byte array',
    algorithm           varchar(32)  not null,
    created_at          timestamp    not null default now(),
    updated_at          timestamp    not null default now() on update now(),
    constraint cc_users_pkey primary key (id)
)
;

create unique index cc_users_id_uindex
    on cc_users (id)
;

create unique index cc_users_username_uindex
    on cc_users (username)
;

create table cc_roles
(
    id          integer      not null auto_increment unique,
    name        varchar(762) not null unique,
    description text,
    created_at  timestamp    not null default now(),
    updated_at  timestamp    not null default now() on update now(),
    constraint cc_roles_pkey primary key (id)
)
;

create unique index cc_roles_id_uindex
    on roles (id)
;

create unique index cc_roles_name_uindex
    on roles (name)
;

create table cc_user_roles
(
    user_id    integer   not null,
    role_id    integer   not null,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now() on update now(),
    constraint cc_user_roles_user_role_pk primary key (user_id, role_id),
    constraint cc_user_roles_users_id_fk foreign key (user_id) references cc_users (id),
    constraint cc_user_roles_roles_id_fk foreign key (role_id) references cc_roles (id)
)
;

create table cc_permissions
(
    id                integer   not null auto_increment unique,
    permission_string text      not null,
    description       text,
    created_at        timestamp not null default now(),
    updated_at        timestamp not null default now() on update now(),
    constraint cc_permissions_pkey primary key (id)
)
;

create table cc_role_permissions
(
    role       integer   not null,
    permission integer   not null,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now() on update now(),
    constraint cc_role_permissions_role_permission_pk primary key (role, permission),
    constraint cc_role_permissions_roles_id_fk foreign key (role) references cc_roles (id),
    constraint cc_role_permissions_permissions_id_fk foreign key (permission) references cc_permissions (id)
)
;

create table cc_user_permissions
(
    user_id    integer   not null,
    permission integer   not null,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now() on update now(),
    constraint cc_user_permissions_user_permission_pk primary key (user_id, permission),
    constraint cc_user_permissions_users_id_fk foreign key (user_id) references cc_users (id),
    constraint cc_user_permissions_permissions_id_fk foreign key (permission) references cc_permissions (id)
)
;

create table rest_api_users
(
    id                  integer      not null auto_increment unique,
    username            varchar(762) not null unique,
    password            text comment 'Base64 encoded raw byte array',
    password_iterations integer      not null,
    password_salt       text         not null comment 'Base64 encoded raw byte array',
    algorithm           varchar(32)  not null,
    created_at          timestamp    not null default now(),
    updated_at          timestamp    not null default now() on update now(),
    constraint rest_api_users_pkey primary key (id)
)
;

create unique index rest_api_users_id_uindex
    on rest_api_users (id)
;

create unique index rest_api_users_username_uindex
    on rest_api_users (username)
;

create table rest_api_roles
(
    id          integer      not null auto_increment unique,
    name        varchar(762) not null unique,
    description text,
    created_at  timestamp    not null default now(),
    updated_at  timestamp    not null default now() on update now(),
    constraint rest_api_roles_pkey primary key (id)
)
;

create unique index rest_api_roles_id_uindex
    on roles (id)
;

create unique index rest_api_roles_name_uindex
    on roles (name)
;

create table rest_api_user_roles
(
    user_id    integer   not null,
    role_id    integer   not null,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now() on update now(),
    constraint rest_api_user_roles_user_role_pk primary key (user_id, role_id),
    constraint rest_api_user_roles_users_id_fk foreign key (user_id) references rest_api_users (id),
    constraint rest_api_user_roles_roles_id_fk foreign key (role_id) references rest_api_roles (id)
)
;

create table rest_api_permissions
(
    id                integer   not null auto_increment unique,
    permission_string text      not null,
    description       text,
    created_at        timestamp not null default now(),
    updated_at        timestamp not null default now() on update now(),
    constraint rest_api_permissions_pkey primary key (id)
)
;

create table rest_api_role_permissions
(
    role       integer   not null,
    permission integer   not null,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now() on update now(),
    constraint rest_api_role_permissions_role_permission_pk primary key (role, permission),
    constraint rest_api_role_permissions_roles_id_fk foreign key (role) references rest_api_roles (id),
    constraint rest_api_role_permissions_permissions_id_fk foreign key (permission) references rest_api_permissions (id)
)
;

create table rest_api_user_permissions
(
    user_id    integer   not null,
    permission integer   not null,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now() on update now(),
    constraint rest_api_user_permissions_user_permission_pk primary key (user_id, permission),
    constraint rest_api_user_permissions_users_id_fk foreign key (user_id) references rest_api_users (id),
    constraint rest_api_user_permissions_permissions_id_fk foreign key (permission) references rest_api_permissions (id)
)
;
