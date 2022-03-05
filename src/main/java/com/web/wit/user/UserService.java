package com.web.wit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }


    public User createUser(User newUser) {
        // Check if user with same unique fields already exists
        User user = userRepository.findUserByUsername(newUser.getUsername());
        if (user == null) {
            return userRepository.insert(newUser);
        }
        return null;
    }
}
