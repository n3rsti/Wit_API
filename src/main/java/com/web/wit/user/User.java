package com.web.wit.user;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class User {
    @Id
    private String id;
    private String username;

    public User(String username) {
        this.username = username;
    }
}
