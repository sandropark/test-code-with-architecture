package com.example.demo.post.domain;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PostTest {

    @Test
    void create() throws Exception {
        // Given
        PostCreate postCreate = PostCreate.builder()
                .content("컨텐츠")
                .writerId(1)
                .build();

        User user = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .nickname("Sandro")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode("1234")
                .build();

        // When
        Post post = Post.create(postCreate, user);

        // Then
        assertThat(post.getContent()).isEqualTo("컨텐츠");
        assertThat(post.getWriter().getEmail()).isEqualTo("test@gmail.com");
        assertThat(post.getWriter().getNickname()).isEqualTo("Sandro");
        assertThat(post.getWriter().getAddress()).isEqualTo("Seoul");
        assertThat(post.getWriter().getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(post.getWriter().getCertificationCode()).isEqualTo("1234");
    }

    @Test
    void update() throws Exception {
        // FIXME
    }

}