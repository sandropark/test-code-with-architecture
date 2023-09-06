package com.example.demo.user.domain;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    User activeUser;
    User pendingUser;

    @BeforeEach
    void setUp() {
        activeUser = getActiveUser();
        pendingUser = getPendingUser();
    }

    @Test
    void create() throws Exception {
        // Given
        UserCreate userCreate = UserCreate.builder()
                .email("test@gmail.com")
                .nickname("Sandro")
                .address("Seoul")
                .build();

        // When
        String code = "code";
        User created = User.create(userCreate, () -> code);

        // Then
        assertThat(created.getStatus()).isEqualTo(UserStatus.PENDING);

        assertThat(created.getId()).isNull();
        assertThat(created.getEmail()).isEqualTo(userCreate.getEmail());
        assertThat(created.getNickname()).isEqualTo(userCreate.getNickname());
        assertThat(created.getAddress()).isEqualTo(userCreate.getAddress());
        assertThat(created.getCertificationCode()).isEqualTo(code);
        assertThat(created.getLastLoginAt()).isNull();
    }

    @Test
    void update() throws Exception {
        // Given
        UserUpdate userUpdate = UserUpdate.builder()
                .nickname("Sandeul")
                .address("Pusan")
                .build();

        // When
        User updated = activeUser.update(userUpdate);

        // Then
        assertThat(updated.getNickname()).isEqualTo(userUpdate.getNickname());
        assertThat(updated.getAddress()).isEqualTo(userUpdate.getAddress());

        assertThat(updated.getId()).isEqualTo(activeUser.getId());
        assertThat(updated.getEmail()).isEqualTo(activeUser.getEmail());
        assertThat(updated.getStatus()).isEqualTo(activeUser.getStatus());
        assertThat(updated.getCertificationCode()).isEqualTo(activeUser.getCertificationCode());
        assertThat(updated.getLastLoginAt()).isEqualTo(activeUser.getLastLoginAt());
    }

    @Test
    void login() throws Exception {
        // Given
        // When
        long now = 10L;
        User logined = activeUser.login(() -> now);

        // Then
        assertThat(logined.getLastLoginAt()).isEqualTo(now);

        assertThat(logined.getId()).isEqualTo(activeUser.getId());
        assertThat(logined.getEmail()).isEqualTo(activeUser.getEmail());
        assertThat(logined.getNickname()).isEqualTo(activeUser.getNickname());
        assertThat(logined.getAddress()).isEqualTo(activeUser.getAddress());
        assertThat(logined.getStatus()).isEqualTo(activeUser.getStatus());
        assertThat(logined.getCertificationCode()).isEqualTo(activeUser.getCertificationCode());
    }

    @Nested
    class Certificate {
        @Test
        void success() throws Exception {
            // Given
            assertThat(pendingUser.getStatus()).isEqualTo(UserStatus.PENDING);

            // When
            User certificated = pendingUser.certificate(pendingUser.getCertificationCode());

            // Then
            assertThat(certificated.getStatus()).isEqualTo(UserStatus.ACTIVE);

            assertThat(certificated.getId()).isEqualTo(pendingUser.getId());
            assertThat(certificated.getEmail()).isEqualTo(pendingUser.getEmail());
            assertThat(certificated.getNickname()).isEqualTo(pendingUser.getNickname());
            assertThat(certificated.getAddress()).isEqualTo(pendingUser.getAddress());
            assertThat(certificated.getCertificationCode()).isEqualTo(pendingUser.getCertificationCode());
            assertThat(certificated.getLastLoginAt()).isEqualTo(pendingUser.getLastLoginAt());
        }

        @Test
        void failure() throws Exception {
            // Given
            assertThat(pendingUser.getStatus()).isEqualTo(UserStatus.PENDING);

            // When & Then
            assertThatThrownBy(() -> pendingUser.certificate("4321"))
                    .isInstanceOf(CertificationCodeNotMatchedException.class)
                    .hasMessage("자격 증명에 실패하였습니다.");
        }
    }

    private static User getActiveUser() {
        User temp = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .nickname("Sandro")
                .address("Seoul")
                .certificationCode("1234")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(1L)
                .build();
        assertThat(temp.getStatus()).isEqualTo(UserStatus.ACTIVE);
        validateNotNullFields(temp);
        return temp;
    }

    private static User getPendingUser() {
        User temp = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .nickname("Sandro")
                .address("Seoul")
                .certificationCode("1234")
                .status(UserStatus.PENDING)
                .lastLoginAt(1L)
                .build();
        assertThat(temp.getStatus()).isEqualTo(UserStatus.PENDING);
        validateNotNullFields(temp);
        return temp;
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
