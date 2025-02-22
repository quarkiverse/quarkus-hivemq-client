insert into cc_roles (name, description)
values ('super_admin', 'has the HIVEMQ_SUPER_ADMIN permission'),
       ('dashboard_viewer', 'can only see the dashboard');

insert into cc_role_permissions (role, permission)
select cc_roles.id, cc_permissions.id
from cc_roles,
     cc_permissions
where cc_roles.name = 'super_admin'
  AND cc_permissions.permission_string = 'HIVEMQ_SUPER_ADMIN'
;

insert into rest_api_roles (name, description)
values ('super_admin', 'has the HIVEMQ_SUPER_ADMIN permission');

insert into rest_api_role_permissions (role, permission)
select rest_api_roles.id, rest_api_permissions.id
from rest_api_roles,
     rest_api_permissions
where rest_api_roles.name = 'super_admin'
  AND rest_api_permissions.permission_string = 'HIVEMQ_SUPER_ADMIN'
;
