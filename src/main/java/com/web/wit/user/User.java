package com.web.wit.user;


import com.web.wit.post.Post;
import com.web.wit.role.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    private String username;

    private List<Post> postList;

    private String password;

    private Collection<Role> roles = new ArrayList<>();

    public User(String username) {
        this.username = username;
    }
}
