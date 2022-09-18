package com.web.wit.post;

import com.web.wit.comment.Comment;
import com.web.wit.comment.CommentService;
import com.web.wit.user.User;
import com.web.wit.user.UserService;
import org.springframework.stereotype.Service;

import javax.el.PropertyNotFoundException;
import java.util.List;

@Service
public class PostFacade {
    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;

    public PostFacade(PostService postService, UserService userService, CommentService commentService) {
        this.postService = postService;
        this.userService = userService;
        this.commentService = commentService;
    }

    public List<Post> getPosts() {
        return postService.getPosts();
    }

    public List<Post> getPostsByAuthor(String author) {
        return postService.getPostsByAuthor(author);
    }

    public MappedPost findPostById(String postId) {
        Post post = postService.findPostById(postId);
        User author = userService.getFullUserByUsername(post.getAuthor());

        MappedPost mappedPost = new MappedPost();
        mappedPost.setId(post.getId());
        mappedPost.setContent(post.getContent());


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

}
