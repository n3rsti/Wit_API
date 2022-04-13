package com.web.wit.user;


import com.web.wit.role.IRoleService;
import com.web.wit.role.Role;
import com.web.wit.role.RoleService;

public class UserFacade {
    private final IUserService userService;
    private final IRoleService roleService;

    public UserFacade(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }
}
