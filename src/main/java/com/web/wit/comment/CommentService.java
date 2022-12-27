package com.web.wit.comment;

import com.web.wit.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    private final MongoTemplate mongoTemplate;

    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public void deleteCommentById(String commentId) {
        commentRepository.deleteById(commentId);
    }

    public Comment findCommentById(String commentId) {
        return commentRepository.findCommentById(commentId);
    }

    public List<Comment> findCommentsByPostId(String postId, int page, int size){
        return commentRepository.findCommentsByPostIdAndParentCommentIdIsNull(postId, PageRequest.of(page, size));
    }

    public int getCommentCountByPostId(String postId){
        return commentRepository.countCommentsByPostId(postId);
    }

    public Comment findTopCommentByPostId(String postId) {
        return commentRepository.findFirstByPostIdAndContentNotNull(postId);
    }
}
