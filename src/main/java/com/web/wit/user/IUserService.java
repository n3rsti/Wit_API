package com.web.wit.user;


import java.util.List;

public interface IUserService {
    List<MappedUser> getUsers();

    MappedUser getUserById(String id);

    User getFullUserById(String id);

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(String id);
}
