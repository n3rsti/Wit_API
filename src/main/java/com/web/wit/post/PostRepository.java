package com.web.wit.post;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    /**
     * @param author author username
     */
    List<Post> findPostsByAuthor(String author);
}
