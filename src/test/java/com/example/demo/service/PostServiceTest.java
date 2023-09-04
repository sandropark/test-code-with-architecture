package com.example.demo.service;

import com.example.demo.TestContext;
import com.example.demo.model.dto.PostCreateDto;
import com.example.demo.model.dto.PostUpdateDto;
import com.example.demo.repository.PostEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.example.demo.TestContext.ACTIVE_ID;
import static com.example.demo.TestContext.POST_ID;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = TestContext.class)
class PostServiceTest {

    @Autowired
    PostService postService;

    @Test
    void getById() throws Exception {
        // When
        PostEntity foundPost = postService.getById(POST_ID);

        // Then
        assertThat(foundPost).isNotNull();
    }

    @Test
    void create() throws Exception {
        // Given
        String content = "content!!";
        PostCreateDto postCreateDto = PostCreateDto.builder()
                .content(content)
                .writerId(ACTIVE_ID)
                .build();

        // When
        PostEntity postEntity = postService.create(postCreateDto);

        // Then
        assertThat(postEntity.getId()).isNotNull();
        assertThat(postEntity.getContent()).isEqualTo(content);
        assertThat(postEntity.getCreatedAt()).isGreaterThan(0);
    }

    @Test
    void update() throws Exception {
        // Given
        String updateContent = "Update content!!!";
        PostUpdateDto postUpdateDto = PostUpdateDto.builder()
                .content(updateContent)
                .build();

        // When
        postService.update(POST_ID, postUpdateDto);

        // Then
        PostEntity updated = postService.getById(POST_ID);
        assertThat(updated.getContent()).isEqualTo(updateContent);
        assertThat(updated.getModifiedAt()).isGreaterThan(0);
    }

}