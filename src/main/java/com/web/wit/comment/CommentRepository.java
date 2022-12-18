package com.web.wit.comment;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    Comment findCommentById(String commentId);

    List<Comment> findCommentsByPostIdAndParentCommentIdIsNull(String postId);

    int countCommentsByPostId(String postId);
}
