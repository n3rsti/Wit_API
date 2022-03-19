package com.web.wit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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


    public User createUser(User user) {
        // Check if user with same unique fields already exists
        User existingUser = userRepository.findUserByUsername(user.getUsername());
        if (existingUser == null) {
            return userRepository.insert(user);
        }
        return null;
    }

    public User updateUser(User user) {
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            return null;
        }
    }

    public void deleteUser(String id) {
        userRepository.deleteUserById(id);
    }
}
