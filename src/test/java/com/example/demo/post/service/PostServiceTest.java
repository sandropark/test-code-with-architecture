package com.example.demo.post.service;

import com.example.demo.mock.FakePostRepository;
import com.example.demo.mock.FakeUserRepository;
import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.post.domain.PostUpdate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PostServiceTest {

    PostService postService;

    @BeforeEach
    void setUp() {
        FakePostRepository postRepository = new FakePostRepository();
        FakeUserRepository userRepository = new FakeUserRepository();
        postService = PostService.builder()
                .postRepository(postRepository)
                .userRepository(userRepository)
                .build();
    }

    @Test
    void getById() throws Exception {
        // When
        Post foundPost = postService.getById(2);

        // Then
        assertThat(foundPost).isNotNull();
    }

    @Test
    void create() throws Exception {
        // Given
        String content = "content!!";
        PostCreate postCreate = PostCreate.builder()
                .content(content)
                .writerId(2)
                .build();

        // When
        Post post = postService.create(postCreate);

        // Then
        assertThat(post.getId()).isNotNull();
        assertThat(post.getContent()).isEqualTo(content);
        assertThat(post.getCreatedAt()).isGreaterThan(0);
    }

    @Test
    void update() throws Exception {
        // Given
        String updateContent = "Update content!!!";
        PostUpdate postUpdate = PostUpdate.builder()
                .content(updateContent)
                .build();

        // When
        postService.update(2, postUpdate);

        // Then
        Post updated = postService.getById(2);
        assertThat(updated.getContent()).isEqualTo(updateContent);
        assertThat(updated.getModifiedAt()).isGreaterThan(0);
    }

}