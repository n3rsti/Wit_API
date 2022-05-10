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

        // map to MappedUser class
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
        /*
        * JOIN postList from Post document collection
        * Post author = User username
        */
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("post")
                .localField("username")
                .foreignField("author")
                .as("postList");
        Aggregation aggregation = Aggregation.newAggregation(lookupOperation);

        // map to MappedUser class
        return mongoTemplate.aggregate(aggregation, "user", MappedUser.class).getMappedResults();
    }


    public User createUser(User user) {
        // encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.insert(user);
    }

    public User updateUser(User user) {
        /*
         * make sure user exists, if user wouldn't exist,
         * new would be created because of mongodb .save() function implementation.
         * this would be unintended behaviour, and we try to leave it for createUser function
         */
        User userDbSavedVersion = userRepository.findUserByUsername(user.getUsername());
        if (userDbSavedVersion != null) {
            /* Make sure any attempt to change
            *
            * */
            if(!userDbSavedVersion.getId().equals(user.getId())){
                throw new DataIntegrityViolationException("Cannot change user ID");
            }


            // encode password
            user.setPassword(passwordEncoder.encode(user.getPassword()));

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
        /*
        * userdetails.User class requires NonNull authorities variable,
        * since we are not using any authorities yet, we will submit empty ArrayList
        * */
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
}
