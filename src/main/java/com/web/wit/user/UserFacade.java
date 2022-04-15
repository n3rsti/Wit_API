package com.web.wit.user;


import com.web.wit.role.IRoleService;
import com.web.wit.role.Role;
import com.web.wit.role.RoleService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userService.loadUserByUsername(username);
    }

    public Role saveRole(Role role) {
        return roleService.saveRole(role);
    }

    public void addRoleToUser(String username, String roleName) {
        User user = userService.findUserByUsername(username);
        Role role = roleService.findRoleByName(roleName);
        user.getRoles().add(role);
        userService.updateUser(user);
    }

    public List<User> getUsers() {
        return userService.getUsers();
    }

    public User getUserById(String id) {
        return userService.getUserById(id);
    }

    public User findUserByUsername(String username) {
        return userService.findUserByUsername(username);
    }

    public User getFullUserById(String id) {
        return userService.getUserById(id);
    }

    public List<User> getFullUserList() {
        return userService.getFullUserList();
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
