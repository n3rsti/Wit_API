package com.web.wit.comment;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Data
@Document
public class Comment {
    @Id
    private String id;

    private String postId;

    private String parentCommentId;

    private String author;

    private String content;

    public MappedComment toMappedComment() {
        return new MappedComment(this.id, this.postId, this.parentCommentId, this.author, this.content, new ArrayList<>());
    }
}
