-- mqtt clients
create table users
(
    id                  integer      not null identity
        constraint users_pkey primary key,
    username            varchar(900) not null unique,
    password            varchar(max),
    password_iterations integer      not null,
    password_salt       varchar(max),
    algorithm           varchar(32)  not null,
    created_at          datetime2 default getutcdate(),
    updated_at          datetime2 default getutcdate()
);

create unique index users_id_uindex
    on users (id);

create unique index users_username_uindex
    on users (username);

create table roles
(
    id          integer      not null identity
        constraint roles_pkey primary key,
    name        varchar(900) not null unique,
    description varchar(max),
    created_at  datetime2 default getutcdate(),
    updated_at  datetime2 default getutcdate()
);

create unique index roles_id_uindex
    on roles (id);

create unique index roles_name_uindex
    on roles (name);

create table user_roles
(
    user_id    integer not null
        constraint user_roles_users_id_fk references users,
    role_id    integer not null
        constraint user_roles_roles_id_fk references roles,
    created_at datetime2 default getutcdate(),
    updated_at datetime2 default getutcdate(),
    constraint user_roles_user_role_pk primary key (user_id, role_id)
);

create table permissions
(
    id                    integer             not null identity
        constraint permissions_pkey primary key,
    topic                 varchar(max)        not null,
    publish_allowed       bit       default 0 not null,
    subscribe_allowed     bit       default 0 not null,
    qos_0_allowed         bit       default 0 not null,
    qos_1_allowed         bit       default 0 not null,
    qos_2_allowed         bit       default 0 not null,
    retained_msgs_allowed bit       default 0 not null,
    shared_sub_allowed    bit       default 0 not null,
    shared_group          varchar(max),
    created_at            datetime2 default getutcdate(),
    updated_at            datetime2 default getutcdate()
);

create table role_permissions
(
    role       integer not null
        constraint role_permissions_roles_id_fk references roles,
    permission integer not null
        constraint role_permissions_permissions_id_fk references permissions,
    created_at datetime2 default getutcdate(),
    updated_at datetime2 default getutcdate(),
    constraint role_permissions_role_permission_pk primary key (role, permission)
);

create table user_permissions
(
    user_id    integer not null
        constraint user_permissions_users_id_fk references users,
    permission integer not null
        constraint user_permissions_permissions_id_fk references permissions,
    created_at datetime2 default getutcdate(),
    updated_at datetime2 default getutcdate(),
    constraint user_permissions_user_permission_pk primary key (user_id, permission)
);
go

-- control center tables
create table cc_users
(
    id                  integer      not null identity
        constraint cc_users_pkey primary key,
    username            varchar(900) not null unique,
    password            varchar(max),
    password_iterations integer      not null,
    password_salt       varchar(max),
    algorithm           varchar(32)  not null,
    created_at          datetime2 default getutcdate(),
    updated_at          datetime2 default getutcdate()
);

create unique index cc_users_id_uindex
    on cc_users (id);

create unique index cc_users_username_uindex
    on cc_users (username);

create table cc_roles
(
    id          integer      not null identity
        constraint cc_roles_pkey primary key,
    name        varchar(900) not null unique,
    description varchar(max),
    created_at  datetime2 default getutcdate(),
    updated_at  datetime2 default getutcdate()
);

create unique index cc_roles_id_uindex
    on cc_roles (id);

create unique index cc_roles_name_uindex
    on cc_roles (name);

create table cc_user_roles
(
    user_id    integer not null
        constraint cc_user_roles_users_id_fk references cc_users,
    role_id    integer not null
        constraint cc_user_roles_roles_id_fk references cc_roles,
    created_at datetime2 default getutcdate(),
    updated_at datetime2 default getutcdate(),
    constraint cc_user_roles_user_role_pk primary key (user_id, role_id)
);

create table cc_permissions
(
    id                integer      not null identity
        constraint cc_permissions_pkey primary key,
    permission_string varchar(max) not null,
    description       varchar(max),
    created_at        datetime2 default getutcdate(),
    updated_at        datetime2 default getutcdate()
);

create table cc_role_permissions
(
    role       integer not null
        constraint cc_role_permissions_roles_id_fk references cc_roles,
    permission integer not null
        constraint cc_role_permissions_permissions_id_fk references cc_permissions,
    created_at datetime2 default getutcdate(),
    updated_at datetime2 default getutcdate(),
    constraint cc_role_permissions_role_permission_pk primary key (role, permission)
);

create table cc_user_permissions
(
    user_id    integer not null
        constraint cc_user_permissions_users_id_fk references cc_users,
    permission integer not null
        constraint cc_user_permissions_permissions_id_fk references cc_permissions,
    created_at datetime2 default getutcdate(),
    updated_at datetime2 default getutcdate(),
    constraint cc_user_permissions_user_permission_pk primary key (user_id, permission)
);
go

-- rest api tables
create table rest_api_users
(
    id                  integer      not null identity
        constraint rest_api_users_pkey primary key,
    username            varchar(900) not null unique,
    password            varchar(max),
    password_iterations integer      not null,
    password_salt       varchar(max),
    algorithm           varchar(32)  not null,
    created_at          datetime2 default getutcdate(),
    updated_at          datetime2 default getutcdate()
);

create unique index rest_api_users_id_uindex
    on rest_api_users (id);

create unique index rest_api_users_username_uindex
    on rest_api_users (username);

create table rest_api_roles
(
    id          integer      not null identity
        constraint rest_api_roles_pkey primary key,
    name        varchar(900) not null unique,
    description varchar(max),
    created_at  datetime2 default getutcdate(),
    updated_at  datetime2 default getutcdate()
);

create unique index rest_api_roles_id_uindex
    on rest_api_roles (id);

create unique index rest_api_roles_name_uindex
    on rest_api_roles (name);

create table rest_api_user_roles
(
    user_id    integer not null
        constraint rest_api_user_roles_users_id_fk references rest_api_users,
    role_id    integer not null
        constraint rest_api_user_roles_roles_id_fk references rest_api_roles,
    created_at datetime2 default getutcdate(),
    updated_at datetime2 default getutcdate(),
    constraint rest_api_user_roles_user_role_pk primary key (user_id, role_id)
);

create table rest_api_permissions
(
    id                integer      not null identity
        constraint rest_api_permissions_pkey primary key,
    permission_string varchar(max) not null,
    description       varchar(max),
    created_at        datetime2 default getutcdate(),
    updated_at        datetime2 default getutcdate()
);

create table rest_api_role_permissions
(
    role       integer not null
        constraint rest_api_role_permissions_roles_id_fk references rest_api_roles,
    permission integer not null
        constraint rest_api_role_permissions_permissions_id_fk references rest_api_permissions,
    created_at datetime2 default getutcdate(),
    updated_at datetime2 default getutcdate(),
    constraint rest_api_role_permissions_role_permission_pk primary key (role, permission)
);

create table rest_api_user_permissions
(
    user_id    integer not null
        constraint rest_api_user_permissions_users_id_fk references rest_api_users,
    permission integer not null
        constraint rest_api_user_permissions_permissions_id_fk references rest_api_permissions,
    created_at datetime2 default getutcdate(),
    updated_at datetime2 default getutcdate(),
    constraint rest_api_user_permissions_user_permission_pk primary key (user_id, permission)
);
go

-- triggers
create trigger users_updated_at_trg
    on users
    for update as
begin
update users
set updated_at = getutcdate()
from users
         inner join deleted d
                    on users.id = d.id
end;
go

create trigger roles_updated_at_trg
    on roles
    for update as
begin
update roles
set updated_at = getutcdate()
from roles
         inner join deleted d
                    on roles.id = d.id
end;
go

create trigger user_roles_updated_at_trg
    on user_roles
    for update as
begin
update user_roles
set updated_at = getutcdate()
from user_roles
         inner join deleted d
                    on user_roles.user_id = d.user_id
                        and user_roles.role_id = d.role_id
end;
go

create trigger permissions_updated_at_trg
    on permissions
    for update as
begin
update permissions
set updated_at = getutcdate()
from permissions
         inner join deleted d
                    on permissions.id = d.id
end;
go

create trigger role_permissions_updated_at_trg
    on role_permissions
    for update as
begin
update role_permissions
set updated_at = getutcdate()
from role_permissions
         inner join deleted d
                    on role_permissions.role = d.role
                        and role_permissions.permission = d.permission
end;
go

create trigger user_permissions_updated_at_trg
    on user_permissions
    for update as
begin
update user_permissions
set updated_at = getutcdate()
from user_permissions
         inner join deleted d
                    on user_permissions.user_id = d.user_id
                        and user_permissions.permission = d.permission
end;
go

-- control center triggers
create trigger cc_users_updated_at_trg
    on cc_users
    for update as
begin
update cc_users
set updated_at = getutcdate()
from cc_users
         inner join deleted d
                    on cc_users.id = d.id
end;
go

create trigger cc_roles_updated_at_trg
    on cc_roles
    for update as
begin
update cc_roles
set updated_at = getutcdate()
from cc_roles
         inner join deleted d
                    on cc_roles.id = d.id
end;
go

create trigger cc_user_roles_updated_at_trg
    on cc_user_roles
    for update as
begin
update cc_user_roles
set updated_at = getutcdate()
from cc_user_roles
         inner join deleted d
                    on cc_user_roles.user_id = d.user_id
                        and cc_user_roles.role_id = d.role_id
end;
go

create trigger cc_permissions_updated_at_trg
    on cc_permissions
    for update as
begin
update cc_permissions
set updated_at = getutcdate()
from cc_permissions
         inner join deleted d
                    on cc_permissions.id = d.id
end;
go

create trigger cc_role_permissions_updated_at_trg
    on cc_role_permissions
    for update as
begin
update cc_role_permissions
set updated_at = getutcdate()
from cc_role_permissions
         inner join deleted d
                    on cc_role_permissions.role = d.role
                        and cc_role_permissions.permission = d.permission
end;
go

create trigger cc_user_permissions_updated_at_trg
    on cc_user_permissions
    for update as
begin
update cc_user_permissions
set updated_at = getutcdate()
from cc_user_permissions
         inner join deleted d
                    on cc_user_permissions.user_id = d.user_id
                        and cc_user_permissions.permission = d.permission
end;
go

-- rest api triggers
create trigger rest_api_users_updated_at_trg
    on rest_api_users
    for update as
begin
update rest_api_users
set updated_at = getutcdate()
from rest_api_users
         inner join deleted d
                    on rest_api_users.id = d.id
end;
go

create trigger rest_api_roles_updated_at_trg
    on rest_api_roles
    for update as
begin
update rest_api_roles
set updated_at = getutcdate()
from rest_api_roles
         inner join deleted d
                    on rest_api_roles.id = d.id
end;
go

create trigger rest_api_user_roles_updated_at_trg
    on rest_api_user_roles
    for update as
begin
update rest_api_user_roles
set updated_at = getutcdate()
from rest_api_user_roles
         inner join deleted d
                    on rest_api_user_roles.user_id = d.user_id
                        and rest_api_user_roles.role_id = d.role_id
end;
go

create trigger rest_api_permissions_updated_at_trg
    on rest_api_permissions
    for update as
begin
update rest_api_permissions
set updated_at = getutcdate()
from rest_api_permissions
         inner join deleted d
                    on rest_api_permissions.id = d.id
end;
go

create trigger rest_api_role_permissions_updated_at_trg
    on rest_api_role_permissions
    for update as
begin
update rest_api_role_permissions
set updated_at = getutcdate()
from rest_api_role_permissions
         inner join deleted d
                    on rest_api_role_permissions.role = d.role
                        and rest_api_role_permissions.permission = d.permission
end;
go

create trigger rest_api_user_permissions_updated_at_trg
    on rest_api_user_permissions
    for update as
begin
update rest_api_user_permissions
set updated_at = getutcdate()
from rest_api_user_permissions
         inner join deleted d
                    on rest_api_user_permissions.user_id = d.user_id
                        and rest_api_user_permissions.permission = d.permission
end;
go
