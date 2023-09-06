package com.example.demo.post.domain;

import com.example.demo.common.service.port.ClockHolder;
import com.example.demo.user.domain.User;
import lombok.*;

@ToString
@EqualsAndHashCode
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Post {
    private final Long id;
    private final String content;
    private final Long createdAt;
    private final Long modifiedAt;
    private final User writer;

    public static Post create(PostCreate postCreate, User user, ClockHolder clockHolder) {
        long now = clockHolder.mills();
        return Post.builder()
                .content(postCreate.getContent())
                .writer(user)
                .createdAt(now)
                .modifiedAt(now)
                .build();
    }

    public Post update(PostUpdate postUpdate, ClockHolder clockHolder) {
        return Post.builder()
                .id(id)
                .content(postUpdate.getContent())
                .writer(writer)
                .createdAt(createdAt)
                .modifiedAt(clockHolder.mills())
                .build();
    }
}
