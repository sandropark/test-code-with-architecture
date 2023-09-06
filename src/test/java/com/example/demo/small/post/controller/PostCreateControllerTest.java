package com.example.demo.small.post.controller;

import com.example.demo.mock.TestContainer;
import com.example.demo.post.controller.PostCreateController;
import com.example.demo.post.controller.response.PostResponse;
import com.example.demo.post.domain.PostCreate;
import com.example.demo.user.controller.response.UserResponse;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.service.port.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class PostCreateControllerTest {

    private static final long MILLIS = 102302424L;
    TestContainer testContainer = TestContainer.builder()
            .millis(MILLIS)
            .build();
    PostCreateController postCreateController = testContainer.getPostCreateController();
    UserRepository userRepository = testContainer.getUserRepository();

    @Test
    void create() throws Exception {
        // Given
        User user = User.builder()
                .id(1L)
                .email("active@gmail.com")
                .nickname("Sandro")
                .address("Seoul")
                .certificationCode("1234")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(1L)
                .build();
        userRepository.save(user);
        PostCreate postCreate = PostCreate.builder()
                .content("안녕")
                .writerId(user.getId())
                .build();

        // When
        ResponseEntity<PostResponse> response = postCreateController.createPost(postCreate);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getContent()).isEqualTo(postCreate.getContent());
        assertThat(response.getBody().getCreatedAt()).isEqualTo(MILLIS);
        assertThat(response.getBody().getModifiedAt()).isEqualTo(MILLIS);
        assertThat(response.getBody().getWriter()).isEqualTo(UserResponse.fromModel(user));
    }

}