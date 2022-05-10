package com.web.wit.user;


import java.util.List;

public interface IUserService {
    List<MappedUser> getUsers();

    MappedUser getUserByUsername(String username);

    User getFullUserByUsername(String username);

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(String username);
}
