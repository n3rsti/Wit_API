package com.web.wit.comment;

import com.web.wit.post.PostFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.el.PropertyNotFoundException;
import java.util.List;

@RestController()
@RequestMapping("/api/v1/")
public class CommentController {
    private final PostFacade postFacade;

    public CommentController(PostFacade postFacade) {
        this.postFacade = postFacade;
    }

    @PostMapping(path = "posts/{postId}/comments/", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createComment(@RequestBody Comment comment, @PathVariable String postId, UriComponentsBuilder builder, Authentication authentication) {
        comment.setPostId(postId);
        // make sure ID will be generated by mongodb, not user
        comment.setId(null);
        // set author to JWT subject
        comment.setAuthor(authentication.getPrincipal().toString());

        try {
            Comment createdComment = postFacade.createComment(comment);

            UriComponents uriComponents = builder.path("/api/v1/posts/{postId}/comments/{commentId}/")
                    .buildAndExpand(createdComment.getPostId(), createdComment.getId());
            return ResponseEntity.created(uriComponents.toUri()).body(createdComment);
        } catch (PropertyNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }


    }

    @DeleteMapping(path = "posts/{postId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable String commentId, Authentication authentication) {
        Comment comment = postFacade.findCommentById(commentId);

        // check if subject from JWT is comment author
        if (!comment.getAuthor().equals(authentication.getPrincipal().toString())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        postFacade.deleteCommentById(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(path = "posts/{postId}/comments/{commentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getComment(@PathVariable String commentId) {
        MappedComment comment = postFacade.findFullCommentById(commentId);
        if (comment == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @GetMapping(path = "posts/{postId}/comments")
    public List<MappedComment> getCommentsByPostId(@PathVariable String postId, @RequestParam(required = false) String skipCount, @RequestParam(required = false) String size) {
        int intSkipCount = 0;
        int intSize = 3;

        if(skipCount != null)
            intSkipCount = Integer.parseInt(skipCount);
        if(size != null)
            intSize = Integer.parseInt(size);

        return postFacade.findCommentsByPostId(postId, intSkipCount, intSize);
    }


}
