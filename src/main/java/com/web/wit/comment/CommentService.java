package com.web.wit.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public void deleteCommentById(String commentId) {
        commentRepository.deleteById(commentId);
    }

    public Comment findCommentById(String commentId) {
        return commentRepository.findCommentById(commentId);
    }
}
