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

    /**
     * @param author author username
     */
    public List<Post> getPostsByAuthor(String author) {
        return postRepository.findPostsByAuthor(author);
    }

    public void createPost(Post post) {
        postRepository.insert(post);
    }
}
