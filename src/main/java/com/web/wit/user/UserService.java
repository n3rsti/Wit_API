package com.web.wit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService, UserDetailsService {
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    private final PasswordEncoder passwordEncoder;


    /* Returns user with joined posts */
    public MappedUser getUserById(String id) {
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("post")
                .localField("_id")
                .foreignField("author")
                .as("postList");
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("_id").is(id)), lookupOperation);
        return mongoTemplate.aggregate(aggregation, "user", MappedUser.class).getUniqueMappedResult();
    }

    /* This method is used mainly for things where we don't need any join operations (e.g. joining postList to user)
    *  and for operations where User class is needed instead of MappedUser
    *  It should never be used as endpoint response, because it contains whole user document from db
    * */
    public User getFullUserById(String id) {
        return userRepository.findUserById(id);
    }

    public List<MappedUser> getUsers() {
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("post")
                .localField("_id")
                .foreignField("author")
                .as("postList");
        Aggregation aggregation = Aggregation.newAggregation(lookupOperation);
        return mongoTemplate.aggregate(aggregation, "user", MappedUser.class).getMappedResults();
    }


    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.insert(user);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(String id) {
        userRepository.deleteUserById(id);
    }

    // NOTE: It is actually loadUserById, but we have to override loadUserByUsername
    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        User user = userRepository.findUserById(id);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }
}
