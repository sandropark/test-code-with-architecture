package com.example.demo.small.user.controller;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.mock.TestContainer;
import com.example.demo.user.controller.UserController;
import com.example.demo.user.controller.response.MyProfileResponse;
import com.example.demo.user.controller.response.UserResponse;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.service.port.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserControllerTest {

    static final long MILLIS = 112324321231L;
    static final String CODE = "aaaaa-bbbbbb-cccccc";
    TestContainer testContainer = TestContainer.builder()
            .millis(MILLIS)
            .certificationCode(CODE)
            .build();

    @Nested
    class GetById {
        UserController userController = testContainer.getUserController();

        @Test
        void success() throws Exception {
            // Given
            User user = getActiveUser();
            testContainer.getUserRepository().save(user);
            UserResponse userResponse = UserResponse.fromModel(user);

            // When
            ResponseEntity<UserResponse> response = userController.getById(user.getId());

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isEqualTo(userResponse);
        }

        @DisplayName("존재하지 않는 ID일 경우 예외가 발생한다.")
        @Test
        void failure() throws Exception {
            // When & Then
            assertThatThrownBy(() -> userController.getById(10L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Users에서 ID 10를 찾을 수 없습니다.");
        }
    }

    @Nested
    class Verify {
        UserController userController;
        UserRepository userRepository;

        @BeforeEach
        void setUp() {
            userController = testContainer.getUserController();
            userRepository = testContainer.getUserRepository();
        }

        @DisplayName("인증 코드로 사용자를 활성화할 수 있다.")
        @Test
        void success() throws Exception {
            // Given
            User pendingUser = getPendingUser();
            userRepository.save(pendingUser);
            assertThat(pendingUser.getStatus()).isEqualTo(UserStatus.PENDING);

            // When
            ResponseEntity<Void> response = userController.verifyEmail(pendingUser.getId(), pendingUser.getCertificationCode());

            // Then
            User user = userRepository.findById(pendingUser.getId()).orElseThrow();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
            assertThat(response.getHeaders().getLocation()).isEqualTo(URI.create("http://localhost:3000"));

            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @DisplayName("잘못된 인증 코드의 경우 예외가 발생한다.")
        @Test
        void failure() throws Exception {
            // Given
            String wrongCertificationCode = "Wrong code";
            User pendingUser = getPendingUser();
            userRepository.save(pendingUser);
            assertThat(pendingUser.getCertificationCode()).isNotEqualTo(wrongCertificationCode);

            // When & Then
            assertThatThrownBy(() -> userController.verifyEmail(pendingUser.getId(), wrongCertificationCode))
                    .isInstanceOf(CertificationCodeNotMatchedException.class)
                    .hasMessage("자격 증명에 실패하였습니다.");
        }
    }

    @Nested
    class GetMyInfo {
        UserController userController = testContainer.getUserController();

        @Test
        void success() throws Exception {
            // Given
            User activeUser = getActiveUser();
            UserRepository userRepository = testContainer.getUserRepository();
            userRepository.save(activeUser);

            // When
            ResponseEntity<MyProfileResponse> response = userController.getMyInfo(activeUser.getEmail());

            // Then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getId()).isEqualTo(activeUser.getId());
            assertThat(response.getBody().getAddress()).isEqualTo(activeUser.getAddress());
            assertThat(response.getBody().getNickname()).isEqualTo(activeUser.getNickname());
            assertThat(response.getBody().getEmail()).isEqualTo(activeUser.getEmail());
            assertThat(response.getBody().getStatus()).isEqualTo(activeUser.getStatus());
            assertThat(response.getBody().getLastLoginAt()).isEqualTo(MILLIS);
        }

        @DisplayName("존재하지 않는 email일 경우 예외가 발생한다.")
        @Test
        void notFound() throws Exception {
            // When
            assertThatThrownBy(() -> userController.getMyInfo("noEmail@gmail.com"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Users에서 EMAIL noEmail@gmail.com를 찾을 수 없습니다.");
        }
    }

    @Test
    void updateMyInfo() throws Exception {
        // Given
        UserRepository userRepository = testContainer.getUserRepository();
        UserController userController = testContainer.getUserController();
        User activeUser = getActiveUser();
        userRepository.save(activeUser);

        UserUpdate userUpdate = UserUpdate.builder()
                .nickname("수정된 닉네임")
                .address("수정된 주소")
                .build();

        // When
        ResponseEntity<MyProfileResponse> response = userController.updateMyInfo(activeUser.getEmail(), userUpdate);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getId()).isEqualTo(activeUser.getId());
        assertThat(response.getBody().getAddress()).isEqualTo(userUpdate.getAddress());
        assertThat(response.getBody().getNickname()).isEqualTo(userUpdate.getNickname());
        assertThat(response.getBody().getEmail()).isEqualTo(activeUser.getEmail());
        assertThat(response.getBody().getStatus()).isEqualTo(activeUser.getStatus());
        assertThat(response.getBody().getLastLoginAt()).isEqualTo(activeUser.getLastLoginAt());
    }

    private static User getActiveUser() {
        return User.builder()
                .id(1L)
                .email("active@gmail.com")
                .nickname("Sandro")
                .address("Seoul")
                .certificationCode("1234")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(1L)
                .build();
    }

    private static User getPendingUser() {
        return User.builder()
                .id(2L)
                .email("pending@gmail.com")
                .nickname("Coco")
                .address("Pusan")
                .certificationCode("4321")
                .status(UserStatus.PENDING)
                .lastLoginAt(2L)
                .build();
    }

}