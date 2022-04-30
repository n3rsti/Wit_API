package com.web.wit.user;

import com.web.wit.role.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    public User getUserByUsername(String username) {
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("post")
                .localField("_username")
                .foreignField("authorId")
                .as("postList");
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("username").is(username)), lookupOperation);
        return mongoTemplate.aggregate(aggregation, "user", User.class).getUniqueMappedResult();
    }

    public List<User> getUsers() {
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("post")
                .localField("username")
                .foreignField("authorId")
                .as("postList");
        Aggregation aggregation = Aggregation.newAggregation(lookupOperation);
        return mongoTemplate.aggregate(aggregation, "user", User.class).getMappedResults();
    }


    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.insert(user);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
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
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
