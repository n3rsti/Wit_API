package com.web.wit.user;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.web.wit.post.Post;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
public class User {
    @Id
    private String id;

    @JsonProperty("username")
    private String username;

    private List<Post> postList;

    public User() {
    }

    public User(String username) {
        this.username = username;
    }
}
