package com.web.wit.user;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    /**
     * Gets user by username
     *
     * @param username Username (main username, unique, uneditable)
     * @return User
     */
    User findUserByUsername(String username);

    /**
     * Delete user by username
     *
     * @param username Username (main username, unique, uneditable)
     */
    void deleteUserByUsername(String username);

    User findUserById(String id);
}
