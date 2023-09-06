package com.example.demo.small.post.controller;

import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.mock.TestContainer;
import com.example.demo.post.controller.PostController;
import com.example.demo.post.controller.response.PostResponse;
import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.post.service.port.PostRepository;
import com.example.demo.user.controller.response.UserResponse;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PostControllerTest {

    private static final long MILLIS = 102302424L;
    TestContainer testContainer = TestContainer.builder()
            .millis(MILLIS)
            .build();
    PostController postController = testContainer.getPostController();
    PostRepository postRepository = testContainer.getPostRepository();

    @Nested
    class GetById {
        @Test
        void success() throws Exception {
            // Given

            Post post = initPost();
            PostResponse expectedPostResponse = PostResponse.fromModel(post);

            // When
            ResponseEntity<PostResponse> response = postController.getPostById(post.getId());

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(expectedPostResponse);
        }

        @Test
        void failure() throws Exception {
            // When & Then
            assertThatThrownBy(() -> postController.getPostById(100L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Posts에서 ID 100를 찾을 수 없습니다.");
        }
    }

    @Test
    void updatePost() throws Exception {
        // Given
        Post post = initPost();
        PostUpdate postUpdate = PostUpdate.builder().content("수정된 내용").build();

        // When
        ResponseEntity<PostResponse> response = postController.updatePost(post.getId(), postUpdate);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getContent()).isEqualTo(postUpdate.getContent());
        assertThat(response.getBody().getModifiedAt()).isEqualTo(MILLIS);
        assertThat(response.getBody().getId()).isEqualTo(post.getId());
        assertThat(response.getBody().getCreatedAt()).isEqualTo(post.getCreatedAt());
        assertThat(response.getBody().getWriter()).isEqualTo(UserResponse.fromModel(post.getWriter()));
    }

    private Post initPost() {
        User user = User.builder()
                .id(1L)
                .email("active@gmail.com")
                .nickname("Sandro")
                .address("Seoul")
                .certificationCode("1234")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(1L)
                .build();
        Post post = Post.builder()
                .id(1L)
                .content("안녕!")
                .createdAt(100L)
                .modifiedAt(100L)
                .writer(user)
                .build();
        return postRepository.save(post);
    }

}