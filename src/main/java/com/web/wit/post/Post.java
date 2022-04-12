package com.web.wit.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Post {
    @Id
    private String id;

    @JsonProperty("author_id")
    private String authorId;

    private String content;

    public Post() {
    }
}
