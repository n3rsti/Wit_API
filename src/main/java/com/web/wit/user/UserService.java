package com.web.wit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService, UserDetailsService {
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    private final PasswordEncoder passwordEncoder;


    /* Returns user with joined posts */
    public MappedUser getUserByUsername(String username) {
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("post")
                .localField("username")
                .foreignField("author")
                .as("postList");
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("username").is(username)), lookupOperation);
        return mongoTemplate.aggregate(aggregation, "user", MappedUser.class).getUniqueMappedResult();
    }

    /* This method is used mainly for things where we don't need any join operations (e.g. joining postList to user)
    *  and for operations where User class is needed instead of MappedUser
    *  It should never be used as endpoint response, because it contains whole user document from db
    * */
    public User getFullUserByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    public List<MappedUser> getUsers() {
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("post")
                .localField("username")
                .foreignField("author")
                .as("postList");
        Aggregation aggregation = Aggregation.newAggregation(lookupOperation);
        return mongoTemplate.aggregate(aggregation, "user", MappedUser.class).getMappedResults();
    }


    public User createUser(User user) {
        if(userRepository.findUserByUsername(user.getUsername()) != null){
            throw new DataIntegrityViolationException("Username is already taken");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.insert(user);
    }

    public User updateUser(User user) {
        User userBeforeUpdate = userRepository.findUserById(user.getId());
        if(userBeforeUpdate != null) {
            // make sure user can't update his username
            user.setUsername(userBeforeUpdate.getUsername());

            if(user.getPassword() == null){
                user.setPassword(userBeforeUpdate.getPassword());
            }
            return userRepository.save(user);
        }
        return null;
    }

    public void deleteUser(String username) {
        userRepository.deleteUserByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
}
