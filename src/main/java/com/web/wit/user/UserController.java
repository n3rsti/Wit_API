package com.web.wit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> createUser(@RequestBody User user, UriComponentsBuilder builder) {
        User createdUser = userService.createUser(user);
        if (createdUser != null) {
            UriComponents uriComponents = builder.path("api/v1/user/{id}").buildAndExpand(user.getId());
            return ResponseEntity.created(uriComponents.toUri()).body(user);
        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> updateUser(@RequestBody User user, @PathVariable String id) {
        // check if ID from request body is equal to ID from URL
        if(!user.getId().equals(id))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        
        // Check if user with provided ID exists
        if(userService.getUserById(id).isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        User updatedUser = userService.updateUser(user);
        if (updatedUser != null)
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.CONFLICT);
    }
}
