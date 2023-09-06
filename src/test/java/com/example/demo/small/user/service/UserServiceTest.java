package com.example.demo.small.user.service;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.mock.FakeMailSender;
import com.example.demo.mock.FakeUserRepository;
import com.example.demo.user.controller.port.UserService;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.service.CertificationService;
import com.example.demo.user.service.UserServiceImpl;
import com.example.demo.user.service.port.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserServiceTest {

    private static final String CERTIFICATION_CODE = "code";
    private static final long NOW = 10L;
    UserService userService;
    UserRepository userRepository;
    User activeUser = getActiveUser();
    User pendingUser = getPendingUser();

    @BeforeEach
    void setUp() {
        userRepository = new FakeUserRepository();
        userService = UserServiceImpl.builder()
                .userRepository(userRepository)
                .certificationService(new CertificationService(new FakeMailSender()))
                .clockHolder(() -> NOW)
                .uuidHolder(() -> CERTIFICATION_CODE)
                .build();
        initUser();
    }

    @Nested
    class GetByEmail {
        @Test
        void success() throws Exception {
            User user = userService.getByEmail(activeUser.getEmail());

            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @DisplayName("실패 - 존재하지 않는 이메일인 경우 예외가 발생한다.")
        @Test
        void noEmail() throws Exception {
            assertThatThrownBy(() -> userService.getByEmail("no@gmail.com"))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @DisplayName("실패 - 사용자 상태가 ACTIVE가 아닌 경우 예외가 발생한다.")
        @Test
        void noActive() throws Exception {
            assertThatThrownBy(() -> userService.getByEmail(pendingUser.getEmail()))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    class GetById {

        @DisplayName("성공 - 존재하는 ID이고 사용자 상태가 ACTIVE이다.")
        @Test
        void success() throws Exception {
            User user = userService.getById(activeUser.getId());

            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @DisplayName("실패 - 존재하지 않는 ID인 경우 예외가 발생한다.")
        @Test
        void noId() throws Exception {
            assertThatThrownBy(() -> userService.getById(10000L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @DisplayName("실패 - 사용자 상태가 ACTIVE가 아닌 경우 예외가 발생한다.")
        @Test
        void noActive() throws Exception {
            assertThatThrownBy(() -> userService.getById(pendingUser.getId()))
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
        assertThat(user.getEmail()).isEqualTo(userCreate.getEmail());
        assertThat(user.getAddress()).isEqualTo(userCreate.getAddress());
        assertThat(user.getNickname()).isEqualTo(userCreate.getNickname());
        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(user.getCertificationCode()).isEqualTo(CERTIFICATION_CODE);
        assertThat(user.getLastLoginAt()).isNull();
    }

    @Test
    void update() throws Exception {
        // Given
        UserUpdate userUpdate = UserUpdate.builder()
                .address("Jeju")
                .nickname("Coco")
                .build();
        Long user1Id = activeUser.getId();

        // When
        userService.update(user1Id, userUpdate);

        // Then
        User user = userService.getById(user1Id);
        assertThat(user.getId()).isEqualTo(user1Id);
        assertThat(user.getEmail()).isEqualTo(activeUser.getEmail());
        assertThat(user.getAddress()).isEqualTo(userUpdate.getAddress());
        assertThat(user.getNickname()).isEqualTo(userUpdate.getNickname());
        assertThat(user.getStatus()).isEqualTo(activeUser.getStatus());
        assertThat(user.getCertificationCode()).isEqualTo(activeUser.getCertificationCode());
        assertThat(user.getLastLoginAt()).isEqualTo(activeUser.getLastLoginAt());
    }

    @Test
    void login() throws Exception {
        // Given
        Long user1Id = activeUser.getId();
        assertThat(activeUser.getLastLoginAt()).isEqualTo(1L);

        // When
        userService.login(user1Id);

        // Then
        User user = userService.getById(user1Id);
        assertThat(user.getLastLoginAt()).isEqualTo(NOW);
    }

    @Nested
    class VerifyEmail {
        @DisplayName("이메일 인증에 성공한 사용자의 상태는 ACTIVE가 된다.")
        @Test
        void success() throws Exception {
            // Given
            Long user2Id = pendingUser.getId();
            assertThat(pendingUser.getStatus()).isEqualTo(UserStatus.PENDING);

            // When
            userService.verifyEmail(user2Id, pendingUser.getCertificationCode());

            // Then
            User user = userService.getById(user2Id);
            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        void wrongCode() throws Exception {
            assertThatThrownBy(() -> userService.verifyEmail(2L, "2938448"))
                    .isInstanceOf(CertificationCodeNotMatchedException.class);
        }
    }

    private void initUser() {
        userRepository.save(activeUser);
        userRepository.save(pendingUser);
    }

    private static User getActiveUser() {
        User temp = User.builder()
                .id(1L)
                .email("active@gmail.com")
                .nickname("Sandro")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode("code")
                .lastLoginAt(1L)
                .build();
        assertThat(temp.getStatus()).isEqualTo(UserStatus.ACTIVE);
        validateNotNullFields(temp);
        return temp;
    }

    private static User getPendingUser() {
        User temp = User.builder()
                .id(2L)
                .email("pending@gmail.com")
                .nickname("Sandro2")
                .address("Pusan")
                .status(UserStatus.PENDING)
                .certificationCode("1234")
                .lastLoginAt(2L)
                .build();
        assertThat(temp.getStatus()).isEqualTo(UserStatus.PENDING);
        validateNotNullFields(temp);
        return temp;
    }

    private static void validateNotNullFields(User user) {
        assertThat(user.getId()).isNotNull();
        assertThat(user.getEmail()).isNotNull();
        assertThat(user.getNickname()).isNotNull();
        assertThat(user.getAddress()).isNotNull();
        assertThat(user.getCertificationCode()).isNotNull();
        assertThat(user.getLastLoginAt()).isNotNull();
    }

}