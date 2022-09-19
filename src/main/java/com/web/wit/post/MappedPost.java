package com.web.wit.post;

import com.web.wit.comment.Comment;
import lombok.Data;

@Data
public class MappedPost {
    private String id;

    private Object author;

    private String content;

    private Comment[] comments;
}
