package com.web.wit.post;

import com.web.wit.comment.Comment;
import com.web.wit.comment.MappedComment;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
@NoArgsConstructor
public class Post {
    @Id
    private String id;

    private String author;

    private String content;

    private List<MappedComment> comments;

    private int commentCount = 0;
}
