package com.web.wit.user;


import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserFacade {
    private final IUserService userService;

    public UserFacade(UserService userService) {
        this.userService = userService;
    }

    public MappedUser getUserByUsername(String username) {
        return userService.getUserByUsername(username);
    }

    public User getFullUserByUsername(String username) {
        return userService.getFullUserByUsername(username);
    }

    public List<MappedUser> getUsers() {
        return userService.getUsers();
    }

    public User createUser(User user) {
        return userService.createUser(user);
    }

    public User updateUser(User user) {
        return userService.updateUser(user);
    }

    public void deleteUser(String username) {
        userService.deleteUser(username);
    }
}
