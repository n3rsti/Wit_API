package com.web.wit.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

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

    public MappedComment findFullCommentById(String commentId) {
        LookupOperation lookupOperation = LookupOperation.newLookup()
                .from("comment")
                .localField("_id.str")
                .foreignField("parentCommentId.str")
                .as("comments");

        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("_id").is(commentId)), lookupOperation);

        MappedComment mappedComment = mongoTemplate.aggregate(aggregation, "comment", MappedComment.class).getUniqueMappedResult();

        if(mappedComment == null){
            return null;
        }


        // TODO: refactor
        // for some reason first joined comment is always the comment itself
        List<Comment> replies = mappedComment.getComments();
        replies.remove(0);

        mappedComment.setComments(replies);

        return mappedComment;
    }
}
