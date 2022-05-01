package com.web.wit.user;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    /**
     * Gets user by username
     *
     * @param id User id (main username)
     * @return User
     */
    User findUserById(String id);

    /**
     * Delete user by id
     *
     * @param id User id (main username)
     */
    void deleteUserById(String id);
}
