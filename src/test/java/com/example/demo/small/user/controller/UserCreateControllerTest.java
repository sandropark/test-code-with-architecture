package com.example.demo.small.user.controller;

import com.example.demo.mock.TestContainer;
import com.example.demo.user.controller.UserCreateController;
import com.example.demo.user.controller.response.UserResponse;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class UserCreateControllerTest {

    private static final String CERTIFICATION_CODE = "aaaa-bbb-cc";
    private final TestContainer testContainer = TestContainer.builder().certificationCode(CERTIFICATION_CODE).build();

    @Test
    void create() throws Exception {
        // Given
        UserCreateController userCreateController = testContainer.getUserCreateController();
        UserCreate userCreate = UserCreate.builder()
                .email("test@gmail.com")
                .nickname("Sandro")
                .address("Seoul")
                .build();

        // When
        ResponseEntity<UserResponse> response = userCreateController.create(userCreate);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getId()).isNotNull();
        assertThat(response.getBody().getEmail()).isEqualTo(userCreate.getEmail());
        assertThat(response.getBody().getNickname()).isEqualTo(userCreate.getNickname());
        assertThat(response.getBody().getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(response.getBody().getLastLoginAt()).isNull();

        User user = testContainer.getUserRepository().findById(0L).orElseThrow();
        assertThat(user.getCertificationCode()).isEqualTo(CERTIFICATION_CODE);
    }

}