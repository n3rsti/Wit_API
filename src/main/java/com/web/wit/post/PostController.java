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
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public List<Post> getPosts(@RequestParam(required = false) String author) {
        if (author == null) {
            return postService.getPosts();
        }
        return postService.getPostsByAuthor(author);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createPost(Authentication authentication, @RequestBody Post post, UriComponentsBuilder builder) {
        // Do not allow custom post ID, it will be generated later in mongoDB
        post.setId(null);


        if (post.getContent() == null || post.getContent().equals("")) {
            return new ResponseEntity<>("Post content cannot be empty", HttpStatus.BAD_REQUEST);
        }


        /* If author wasn't specified, assign JWT subject value to author field  */
        if (post.getAuthor() == null) {
            post.setAuthor(authentication.getPrincipal().toString());
        }
        /* Check for attempt to create Post with different author */
        else if (!Objects.equals(post.getAuthor(), authentication.getPrincipal().toString())) {
            return new ResponseEntity<>("Cannot create post with different author", HttpStatus.UNAUTHORIZED);
        }
        try {
            postService.createPost(post);

            // set HTTP Location header to POST URI
            UriComponents uriComponents = builder.path("api/v1/posts/{id}").buildAndExpand(post.getId());
            return ResponseEntity.created(uriComponents.toUri()).body(post);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

    }
}
