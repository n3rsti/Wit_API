package com.web.wit.post;

import com.web.wit.comment.Comment;
import com.web.wit.comment.CommentService;
import com.web.wit.comment.MappedComment;
import com.web.wit.user.User;
import com.web.wit.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import javax.el.PropertyNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostFacade {
    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;

    private final MongoTemplate mongoTemplate;

    public List<Post> getPosts() {
        return postService.getPosts();
    }

    public List<Post> getPostsByAuthor(String author) {
        return postService.getPostsByAuthor(author);
    }

    public MappedPost findPostById(String postId) {
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("comment")
                .localField("_id.str")
                .foreignField("postId.str")
                .as("comments");

        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("_id").is(postId)), lookupOperation);

        MappedPost mappedPost = mongoTemplate.aggregate(aggregation, "post", MappedPost.class).getUniqueMappedResult();
        if(mappedPost == null)
            return null;


        // TODO: this is shit, but idk how to do it yet
        List<Comment> comments = new ArrayList<>();

        for(Comment comment : mappedPost.getComments()){
            if(comment.getParentCommentId() == null){
                comments.add(comment);
            }
        }

        mappedPost.setComments(comments);

        Post post = postService.findPostById(postId);
        User author = userService.getFullUserByUsername(post.getAuthor());


        mappedPost.setAuthor(author);

        return mappedPost;
    }

    public Post createPost(Post post) {
        return postService.createPost(post);
    }

    public Comment createComment(Comment comment){
        Post post = postService.findPostById(comment.getPostId());
        if(post == null){
            throw new PropertyNotFoundException("Post ID: " + comment.getPostId() + " doesn't exist");
        }
        if(comment.getParentCommentId() != null){
            Comment parentComment = commentService.findCommentById(comment.getParentCommentId());
            if(parentComment == null)
                throw new PropertyNotFoundException("Comment ID: " + comment.getParentCommentId() + " doesn't exist");
        }
        return commentService.createComment(comment);
    }

    public void deleteCommentById(String commentId){
        commentService.deleteCommentById(commentId);
    }

    public Comment findCommentById(String commentId){
        return commentService.findCommentById(commentId);
    }

    public MappedComment findFullCommentById(String commentId){
        return commentService.findFullCommentById(commentId);
    }

}
