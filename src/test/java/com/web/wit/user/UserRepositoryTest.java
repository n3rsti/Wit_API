package com.web.wit.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void cleanUp() {
        this.userRepository.deleteAll();
    }

    @Test
    void findUserByUsernameShouldReturnValidUserObject() {
        User user = new User("test");
        userRepository.save(user);

        User actual = userRepository.findUserByUsername("test");
        Assertions.assertEquals(user, actual);
    }
}
