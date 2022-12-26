package com.web.wit.post;

import com.web.wit.comment.Comment;
import com.web.wit.comment.MappedComment;
import lombok.Data;

import java.util.List;

@Data
public class MappedPost {
    private String id;

    private Object author;

    private String content;

    private List<MappedComment> comments;

    private int commentCount = 0;
}
