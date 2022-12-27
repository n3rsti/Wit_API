package com.web.wit.comment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    Comment findCommentById(String commentId);

    List<Comment> findCommentsByPostIdAndParentCommentIdIsNull(String postId, Pageable pageable);

    int countCommentsByPostId(String postId);

    Comment findFirstByPostIdAndContentNotNull(String postId);
}
