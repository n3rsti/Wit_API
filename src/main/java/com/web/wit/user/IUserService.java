package com.web.wit.user;


import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface IUserService {
    List<User> getUsers();

    User getUserById(String id);

    User findUserByUsername(String username);

    User getFullUserById(String id);

    List<User> getFullUserList();

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(String id);

    UserDetails loadUserByUsername(String username);
}
