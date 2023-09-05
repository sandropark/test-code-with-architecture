package com.example.demo.post.domain;

import com.example.demo.user.domain.User;
import lombok.*;

import java.time.Clock;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Post {
    private Long id;
    private String content;
    private Long createdAt;
    private Long modifiedAt;
    private User writer;

    public static Post create(PostCreate postCreate, User user) {
        return Post.builder()
                .content(postCreate.getContent())
                .writer(user)
                .createdAt(Clock.systemUTC().millis())
                .build();
    }

    public void update(PostUpdate postUpdate) {
        content = postUpdate.getContent();
        modifiedAt = Clock.systemUTC().millis();
    }
}
