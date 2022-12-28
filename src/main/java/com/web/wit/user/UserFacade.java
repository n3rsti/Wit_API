package com.web.wit.user;


import com.web.wit.comment.Comment;
import com.web.wit.comment.CommentService;
import com.web.wit.comment.MappedComment;
import com.web.wit.post.Post;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserFacade {
    private final IUserService userService;

    private final CommentService commentService;

    private final MongoTemplate mongoTemplate;

    public UserFacade(UserService userService, CommentService commentService, MongoTemplate mongoTemplate) {
        this.userService = userService;
        this.commentService = commentService;
        this.mongoTemplate = mongoTemplate;
    }

    public MappedUser getUserByUsername(String username) {
        /* Join post collection with user collection  */
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("post")
                .localField("username")
                .foreignField("author")
                .as("postList");

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("username").is(username)),
                lookupOperation
        );

        /* For each post in postList, join comment collection with post collection */
        MappedUser mappedUser = mongoTemplate.aggregate(aggregation, "user", MappedUser.class).getUniqueMappedResult();

        if(mappedUser != null){
            for (Post post : mappedUser.getPostList()) {
                LookupOperation lookupOperation1 = LookupOperation.newLookup()
                        .from("user")
                        .localField("author")
                        .foreignField("username")
                        .as("author");

                // Aggregate lookup where post id is the post id of the comment
                Aggregation aggregation1 = Aggregation.newAggregation(
                        Aggregation.match(Criteria.where("postId").is(post.getId())),
                        lookupOperation1,
                        Aggregation.limit(1)
                );

                List<MappedComment> comments = mongoTemplate.aggregate(aggregation1, "comment", MappedComment.class).getMappedResults();

                int commentCount = commentService.getCommentCountByPostId(post.getId());

                post.setComments(comments);
                post.setCommentCount(commentCount);
            }
        }



        return mappedUser;
    }

    public User getFullUserByUsername(String username) {
        return userService.getFullUserByUsername(username);
    }

    public List<MappedUser> getUsers() {
        return userService.getUsers();
    }

    public User createUser(User user) {
        return userService.createUser(user);
    }

    public User updateUser(User user) {
        return userService.updateUser(user);
    }

    public void deleteUser(String username) {
        userService.deleteUser(username);
    }
}
