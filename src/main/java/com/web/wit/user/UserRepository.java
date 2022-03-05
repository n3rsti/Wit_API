package com.web.wit.user;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    /**
     * Gets user by username
     *
     * @param username - User nickname / username
     * @return User
     */
    User findUserByUsername(String username);
}
