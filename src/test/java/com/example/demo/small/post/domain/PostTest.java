package com.example.demo.small.post.domain;

import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PostTest {

    User user;
    Post post;

    @BeforeEach
    void setUp() {
        user = getUser();
        post = getPost();
    }

    @Test
    void create() throws Exception {
        // Given
        PostCreate postCreate = PostCreate.builder()
                .content("컨텐츠")
                .writerId(user.getId())
                .build();

        // When
        long now = 10L;
        Post post = Post.create(postCreate, user, () -> now);

        // Then
        assertThat(post.getId()).isNull();
        assertThat(post.getContent()).isEqualTo(postCreate.getContent());
        assertThat(post.getCreatedAt()).isEqualTo(now);
        assertThat(post.getModifiedAt()).isEqualTo(now);
        assertThat(post.getWriter()).isEqualTo(user);
    }

    @Test
    void update() throws Exception {
        // Given
        PostUpdate postUpdate = PostUpdate.builder()
                .content("Update content!!!")
                .build();

        // When
        long now = 15L;
        Post updated = post.update(postUpdate, () -> now);

        // Then
        assertThat(updated.getId()).isEqualTo(post.getId());
        assertThat(updated.getContent()).isEqualTo(postUpdate.getContent());
        assertThat(updated.getCreatedAt()).isEqualTo(post.getCreatedAt());
        assertThat(updated.getModifiedAt()).isEqualTo(now);
        assertThat(updated.getWriter()).isEqualTo(user);
    }

    private User getUser() {
        User temp = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .nickname("Sandro")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode("1234")
                .lastLoginAt(1L)
                .build();
        assertThat(temp.getStatus()).isEqualTo(UserStatus.ACTIVE);
        validateNotNullFields(temp);
        return temp;
    }

    private Post getPost() {
        Post temp = Post.builder()
                .id(1L)
                .content("content")
                .createdAt(10L)
                .modifiedAt(10L)
                .writer(user)
                .build();
        validateNotNullFields(temp);
        return temp;
    }

    private static void validateNotNullFields(Post temp) {
        assertThat(temp.getId()).isNotNull();
        assertThat(temp.getContent()).isNotNull();
        assertThat(temp.getCreatedAt()).isNotNull();
        assertThat(temp.getModifiedAt()).isNotNull();
        assertThat(temp.getWriter()).isNotNull();
    }

    private static void validateNotNullFields(User temp) {
        assertThat(temp.getId()).isNotNull();
        assertThat(temp.getEmail()).isNotNull();
        assertThat(temp.getNickname()).isNotNull();
        assertThat(temp.getAddress()).isNotNull();
        assertThat(temp.getCertificationCode()).isNotNull();
        assertThat(temp.getLastLoginAt()).isNotNull();
    }

}