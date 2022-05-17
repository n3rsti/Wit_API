package com.web.wit.comment;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Comment {
    @Id
    private String id;

    private String postId;

    private String author;

    private String content;
}
