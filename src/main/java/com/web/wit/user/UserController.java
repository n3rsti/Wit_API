package com.web.wit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
public class UserController {
    private final UserFacade userFacade;

    @Autowired
    public UserController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @GetMapping
    public List<User> getUsers() {
        return userFacade.getFullUserList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        User user = userFacade.getFullUserById(id);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> createUser(@RequestBody User user, UriComponentsBuilder builder) {
        try {
            userFacade.createUser(user);
            UriComponents uriComponents = builder.path("api/v1/users/{id}").buildAndExpand(user.getId());
            return ResponseEntity.created(uriComponents.toUri()).body(user);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUser(@RequestBody User user, @PathVariable String id) {
        // check if ID from request body is equal to ID from URL
        if (user.getId() != null && !user.getId().equals(id))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID from URI is not equal to ID from request body");

        // Check if user with provided ID exists
        if (userFacade.getUserById(id) == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (user.getId() == null)
            user.setId(id);

        try {
            User updatedUser = userFacade.updateUser(user);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userFacade.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
