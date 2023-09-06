package com.example.demo.small.post.controller.response;

import com.example.demo.post.controller.response.PostResponse;
import com.example.demo.post.domain.Post;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PostResponseTest {

    @Test
    void fromModel() throws Exception {
        // Given
        User user = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .nickname("Sandro")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode("1234")
                .lastLoginAt(1L)
                .build();

        Post post = Post.builder()
                .content("content")
                .writer(user)
                .build();

        // When
        PostResponse postResponse = PostResponse.fromModel(post);

        // Then
        assertThat(postResponse.getContent()).isEqualTo("content");
        assertThat(postResponse.getWriter().getEmail()).isEqualTo("test@gmail.com");
        assertThat(postResponse.getWriter().getNickname()).isEqualTo("Sandro");
        assertThat(postResponse.getWriter().getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(postResponse.getWriter().getLastLoginAt()).isEqualTo(1L);
    }

}