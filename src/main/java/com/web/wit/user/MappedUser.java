package com.web.wit.user;


import com.web.wit.post.Post;
import lombok.Data;

import java.util.List;

@Data
public class MappedUser {
    private String id;

    private String username;

    private List<Post> postList;
}
