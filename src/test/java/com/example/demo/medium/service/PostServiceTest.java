package com.example.demo.medium.service;

import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.post.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.assertThat;

@SqlGroup({
        @Sql(value = "/sql/post-service-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
})
@SpringBootTest
class PostServiceTest {

    @Autowired
    PostService postService;

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