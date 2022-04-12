package com.web.wit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final MongoTemplate mongoTemplate;

    @Autowired
    public UserService(UserRepository userRepository, MongoTemplate mongoTemplate) {
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    /* Returns user with joined posts */
    public User getFullUserById(String id) {
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("post")
                .localField("_id")
                .foreignField("authorId")
                .as("postList");
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("_id").is(id)), lookupOperation);
        return mongoTemplate.aggregate(aggregation, "user", User.class).getUniqueMappedResult();
    }

    public List<User> getFullUserList() {
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("post")
                .localField("_id")
                .foreignField("authorId")
                .as("postList");
        Aggregation aggregation = Aggregation.newAggregation(lookupOperation);
        return mongoTemplate.aggregate(aggregation, "user", User.class).getMappedResults();
    }


    public User createUser(User user) {
        return userRepository.insert(user);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(String id) {
        userRepository.deleteUserById(id);
    }
}
