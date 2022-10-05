package com.web.wit.comment;

import lombok.Data;

import java.util.List;

@Data
public class MappedComment {
    private String id;

    private String postId;

    private String parentCommentId;

    private Object author;

    private String content;

    private List<Comment> comments;
}
