package com.web.wit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/users")
public class UserController {
    private final UserFacade userFacade;

    @Autowired
    public UserController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @GetMapping
    public List<MappedUser> getUsers() {
        return userFacade.getUsers();
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        MappedUser user = userFacade.getUserByUsername(username);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> createUser(@RequestBody User user, UriComponentsBuilder builder) {
        try {
            userFacade.createUser(user);
            UriComponents uriComponents = builder.path("api/v1/users/{username}").buildAndExpand(user.getUsername());
            return ResponseEntity.created(uriComponents.toUri()).body(user);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

    }

    @PutMapping(path = "/{username}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUser(@RequestBody User user, @PathVariable String username) {
        if (!username.equals(user.getUsername())) {
            return new ResponseEntity<>("Username cannot be changed. Username from URL is not equal to username from body", HttpStatus.CONFLICT);
        }

        try {
            User updatedUser = userFacade.updateUser(user);

            // if user doesn't exist, updatedUser will be null
            if(updatedUser == null){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }

    }

    @PatchMapping(path="/{username}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> patchUser(@RequestBody User user, @PathVariable String username){
        if (user.getUsername() != null && !username.equals(user.getUsername())) {
            return new ResponseEntity<>("Username cannot be changed. Username from URL is not equal to username from body", HttpStatus.CONFLICT);
        }

        User userDbSavedVersion = userFacade.getFullUserByUsername(username);

        // Check if user with provided username exists
        if (userDbSavedVersion == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if(user.getId() == null)
            user.setId(userDbSavedVersion.getId());
        if(user.getUsername() == null)
            user.setUsername(userDbSavedVersion.getUsername());
        if(user.getPassword() == null)
            user.setPassword(userDbSavedVersion.getPassword());

        try {
            User updatedUser = userFacade.updateUser(user);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping(path = "/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        userFacade.deleteUser(username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
