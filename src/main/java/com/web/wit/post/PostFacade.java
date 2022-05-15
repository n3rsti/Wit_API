package com.web.wit.post;

import com.web.wit.user.User;
import com.web.wit.user.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostFacade {
    private final PostService postService;
    private final UserService userService;

    public PostFacade(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
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

}
