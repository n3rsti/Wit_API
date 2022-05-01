package com.web.wit.user;


import com.web.wit.role.IRoleService;
import com.web.wit.role.Role;
import com.web.wit.role.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserFacade {
    private final IUserService userService;
    private final IRoleService roleService;

    public UserFacade(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    public Role saveRole(Role role) {
        return roleService.saveRole(role);
    }

    public void addRoleToUser(String id, String roleName) {
        User user = userService.getUserById(id);
        Role role = roleService.findRoleByName(roleName);
        user.getRoles().add(role);
        userService.updateUser(user);
    }

    public User getUserById(String id) {
        return userService.getUserById(id);
    }

    public List<User> getUsers() {
        return userService.getUsers();
    }

    public User createUser(User user) {
        return userService.createUser(user);
    }

    public User updateUser(User user) {
        return userService.updateUser(user);
    }

    public void deleteUser(String id) {
        userService.deleteUser(id);
    }
}
