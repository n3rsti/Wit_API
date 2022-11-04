package com.web.wit.post;

import com.web.wit.comment.Comment;
import lombok.Data;

import java.util.List;

@Data
public class MappedPost {
    private String id;

    private Object author;

    private String content;

    private List<Comment> comments;

    private int commentCount = 0;
}
