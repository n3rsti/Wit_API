package com.web.wit.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> getPosts() {
        return postRepository.findAll();
    }

    public List<Post> getPostsByAuthorId(String authorId) {
        return postRepository.findPostsByAuthorId(authorId);
    }

    public void createPost(Post post) {
        postRepository.insert(post);
    }
}
