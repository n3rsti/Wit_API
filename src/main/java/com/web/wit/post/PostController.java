package com.web.wit.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("api/v1/posts")
public class PostController {
    private final PostFacade postFacade;

    @Autowired
    public PostController(PostFacade postFacade) {
        this.postFacade = postFacade;
    }

    @GetMapping
    public List<Post> getPosts(@RequestParam(required = false) String author) {
        if (author == null) {
            return postFacade.getPosts();
        }
        return postFacade.getPostsByAuthor(author);
    }

    @GetMapping(path="/{postId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findPostById(@PathVariable String postId){
        MappedPost post = postFacade.findPostById(postId);
        if(post == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createPost(Authentication authentication, @RequestBody Post post, UriComponentsBuilder builder) {
        // Do not allow custom post ID, it will be generated later in mongoDB
        post.setId(null);

        // Set author to JWT Subject
        post.setAuthor(authentication.getPrincipal().toString());


        if (post.getContent() == null || post.getContent().equals("")) {
            return new ResponseEntity<>("Post content cannot be empty", HttpStatus.BAD_REQUEST);
        }
        try {
            Post createdPost = postFacade.createPost(post);

            // set HTTP Location header to POST URI
            UriComponents uriComponents = builder.path("api/v1/posts/{id}").buildAndExpand(post.getId());
            return ResponseEntity.created(uriComponents.toUri()).body(createdPost);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(Authentication authentication, @PathVariable String postId){
        Post post = postFacade.findPostInfoById(postId);
        if(!Objects.equals(post.getAuthor(), authentication.getPrincipal().toString()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        postFacade.deletePost(post);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
