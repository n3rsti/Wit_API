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
import java.util.Map;

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

    public List<MappedPost> getPostsWithAuthor(){
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("user")
                .localField("author")
                .foreignField("username")
                .as("author");

        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("content").ne(null)), lookupOperation);

        List<MappedPost> postList = mongoTemplate.aggregate(aggregation, "post", MappedPost.class).getMappedResults();

        for(MappedPost post : postList){
            int commentCount = commentService.getCommentCountByPostId(post.getId());

            List<MappedComment> mappedComments = new ArrayList<>();

            Comment comment = commentService.findTopCommentByPostId(post.getId());
            if(comment != null){
                MappedComment mappedTopComment = comment.toMappedComment();

                User topCommentAuthor = userService.getUserByUsername(comment.getAuthor());
                mappedTopComment.setAuthor(topCommentAuthor);

                mappedComments.add(mappedTopComment);
            }










            post.setComments(mappedComments);
            post.setCommentCount(commentCount);

        }

        return postList;

    }

    public List<Post> getPostsByAuthor(String author) {
        return postService.getPostsByAuthor(author);
    }

    // without any JOIN operations, only info from DB
    public Post findPostInfoById(String postId) {
        return postService.findPostById(postId);
    }

    public MappedPost findPostById(String postId) {
        LookupOperation commentJoinOperation = LookupOperation.newLookup()
                .from("comment")
                .localField("_id.str")
                .foreignField("postId.str")
                .as("comments");


        LookupOperation authorJoinOperation = LookupOperation.newLookup()
                .from("user")
                .localField("author")
                .foreignField("username")
                .as("author");


        /* This comment is in honor of github copilot which solved my weeks long problem in 5 min */
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("_id").is(postId)), authorJoinOperation, commentJoinOperation);

        MappedPost mappedPost = mongoTemplate.aggregate(aggregation, "post", MappedPost.class).getUniqueMappedResult();
        if (mappedPost == null)
            return null;


        // TODO: this is shit, but idk how to do it yet
        List<MappedComment> comments = new ArrayList<>();

        for (MappedComment comment : mappedPost.getComments()) {
            if (comment.getParentCommentId() == null) {
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

    public Comment createComment(Comment comment) {
        Post post = postService.findPostById(comment.getPostId());
        if (post == null) {
            throw new PropertyNotFoundException("Post ID: " + comment.getPostId() + " doesn't exist");
        }
        if (comment.getParentCommentId() != null) {
            Comment parentComment = commentService.findCommentById(comment.getParentCommentId());
            if (parentComment == null)
                throw new PropertyNotFoundException("Comment ID: " + comment.getParentCommentId() + " doesn't exist");
        }
        return commentService.createComment(comment);
    }

    public void deleteCommentById(String commentId) {
        commentService.deleteCommentById(commentId);
    }

    public Comment findCommentById(String commentId) {
        return commentService.findCommentById(commentId);
    }

    public MappedComment findFullCommentById(String commentId) {
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("comment")
                .localField("_id.str")
                .foreignField("parentCommentId.str")
                .as("comments");

        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("_id").is(commentId)), lookupOperation);

        MappedComment mappedComment = mongoTemplate.aggregate(aggregation, "comment", MappedComment.class).getUniqueMappedResult();

        if (mappedComment == null) {
            return null;
        }


        // TODO: refactor
        // for some reason first joined comment is always the comment itself
        List<Comment> replies = mappedComment.getComments();
        replies.remove(0);

        mappedComment.setComments(replies);

        User author = userService.getFullUserByUsername((String) mappedComment.getAuthor());
        mappedComment.setAuthor(author);


        return mappedComment;
    }

    public void deletePost(Post post) {
        postService.deletePost(post);
    }

    public List<Comment> findCommentsByPostId(String postId){
        return commentService.findCommentsByPostId(postId);
    }
}
