package com.web.wit.user;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class User {
    @Id
    private String id;

    @JsonProperty("username")
    private String username;

    public User() {
    }

    public User(String username) {
        this.username = username;
    }
}
