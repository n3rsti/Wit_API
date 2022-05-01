package com.web.wit.user;


import java.util.List;

public interface IUserService {
    List<User> getUsers();

    User getUserById(String id);

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(String id);
}
