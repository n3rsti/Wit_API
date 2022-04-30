package com.web.wit.user;


import java.util.List;

public interface IUserService {
    List<User> getUsers();

    User getUserByUsername(String username);

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(String username);
}
