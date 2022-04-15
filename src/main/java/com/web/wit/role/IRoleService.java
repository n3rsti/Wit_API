package com.web.wit.role;

public interface IRoleService {
    Role saveRole(Role role);
    Role findRoleByName(String roleName);
}
