package com.example.demo.user.service;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.mock.FakeMailSender;
import com.example.demo.mock.FakeUserRepository;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserServiceTest {

    UserService userService;
    FakeUserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new FakeUserRepository();
        User user1 = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .nickname("Sandro")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode("code")
                .lastLoginAt(1L)
                .build();
        User user2 = User.builder()
                .id(2L)
                .email("test2@gmail.com")
                .nickname("Sandro2")
                .address("Pusan")
                .status(UserStatus.PENDING)
                .certificationCode("1234")
                .lastLoginAt(2L)
                .build();
        userRepository.save(user1);
        userRepository.save(user2);

        userService = UserService.builder()
                .userRepository(userRepository)
                .certificationService(new CertificationService(new FakeMailSender()))
                .clockHolder(() -> 10L)
                .uuidHolder(() -> "code")
                .build();
    }

    @Nested
    class GetByEmail {
        @Test
        void success() throws Exception {
            User user = userService.getByEmail("test@gmail.com");

            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        void failure() throws Exception {
            assertThatThrownBy(() -> userService.getByEmail("inactive@gmail.com"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    class GetById {
        @Test
        void success() throws Exception {
            User user = userService.getById(1);

            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        void failure() throws Exception {
            assertThatThrownBy(() -> userService.getById(100))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Test
    void create() throws Exception {
        // Given
        UserCreate userCreate = UserCreate.builder()
                .email("test@gmail.com")
                .address("Seoul")
                .nickname("Sandro")
                .build();

        // When
        User user = userService.create(userCreate);

        // Then
        assertThat(user.getId()).isNotNull();
        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(user.getCertificationCode()).isEqualTo("code");
    }

    @Test
    void update() throws Exception {
        // Given
        String address = "Jeju";
        String nickname = "Coco";
        UserUpdate userUpdate = UserUpdate.builder()
                .address(address)
                .nickname(nickname)
                .build();

        // When
        User user = userService.update(1, userUpdate);

        // Then
        assertThat(user.getAddress()).isEqualTo(address);
        assertThat(user.getNickname()).isEqualTo(nickname);
    }

    @Test
    void login() throws Exception {
        // Given
        // When
        userService.login(1);

        // Then
        User user = userService.getById(1);
        assertThat(user.getLastLoginAt()).isEqualTo(10L);
    }

    @Nested
    class VerifyEmail {
        @Test
        void success() throws Exception {
            // When
            userService.verifyEmail(2, "1234");

            // Then
            User user = userService.getById(2);
            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        void failure() throws Exception {
            assertThatThrownBy(() -> userService.verifyEmail(2, "2938448"))
                    .isInstanceOf(CertificationCodeNotMatchedException.class);
        }
    }

}