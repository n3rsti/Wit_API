package com.web.wit.post;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@NoArgsConstructor
public class Post {
    @Id
    private String id;

    private String author;

    private String content;

    private int commentCount = 0;
}
