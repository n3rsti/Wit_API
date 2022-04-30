package com.web.wit.user;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    /**
     * Gets user by username
     *
     * @param username - User nickname / username
     * @return User
     */
    User findUserByUsername(String username);

    /**
     * Delete user by id
     *
     * @param username - User nickname / username
     */
    void deleteUserByUsername(String username);
}
