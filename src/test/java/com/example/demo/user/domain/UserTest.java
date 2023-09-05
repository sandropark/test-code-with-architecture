package com.example.demo.user.domain;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    @Test
    void create() throws Exception {
        // Given
        UserCreate userCreate = UserCreate.builder()
                .email("test@gmail.com")
                .nickname("Sandro")
                .address("Seoul")
                .build();
        String code = "code";

        // When
        User created = User.create(userCreate, () -> code);

        // Then
        assertThat(created.getId()).isNull();
        assertThat(created.getEmail()).isEqualTo("test@gmail.com");
        assertThat(created.getNickname()).isEqualTo("Sandro");
        assertThat(created.getAddress()).isEqualTo("Seoul");
        assertThat(created.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(created.getCertificationCode()).isEqualTo(code);
        assertThat(created.getLastLoginAt()).isNull();
    }

    @Test
    void update() throws Exception {
        // Given
        User user = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .nickname("Sandro")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode("1234")
                .lastLoginAt(1L)
                .build();

        UserUpdate userUpdate = UserUpdate.builder()
                .nickname("Sandeul")
                .address("Pusan")
                .build();

        // When
        User updated = user.update(userUpdate);

        // Then
        assertThat(updated.getId()).isEqualTo(1L);
        assertThat(updated.getEmail()).isEqualTo("test@gmail.com");
        assertThat(updated.getNickname()).isEqualTo("Sandeul");
        assertThat(updated.getAddress()).isEqualTo("Pusan");
        assertThat(updated.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(updated.getCertificationCode()).isEqualTo("1234");
        assertThat(updated.getLastLoginAt()).isEqualTo(1L);
    }

    @Test
    void login() throws Exception {
        // Given
        User user = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .nickname("Sandro")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode("1234")
                .lastLoginAt(1L)
                .build();

        // When
        User logined = user.login(() -> 10L);

        // Then
        assertThat(logined.getId()).isEqualTo(1L);
        assertThat(logined.getEmail()).isEqualTo("test@gmail.com");
        assertThat(logined.getNickname()).isEqualTo("Sandro");
        assertThat(logined.getAddress()).isEqualTo("Seoul");
        assertThat(logined.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(logined.getCertificationCode()).isEqualTo("1234");
        assertThat(logined.getLastLoginAt()).isEqualTo(10L);
    }

    @Nested
    class Certificate {
        @Test
        void success() throws Exception {
            // Given
            User user = User.builder()
                    .id(1L)
                    .email("test@gmail.com")
                    .nickname("Sandro")
                    .address("Seoul")
                    .status(UserStatus.PENDING)
                    .certificationCode("1234")
                    .lastLoginAt(1L)
                    .build();
            assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING);

            // When
            User certificated = user.certificate("1234");

            // Then
            assertThat(certificated.getStatus()).isEqualTo(UserStatus.ACTIVE);
            assertThat(certificated.getId()).isEqualTo(1L);
            assertThat(certificated.getEmail()).isEqualTo("test@gmail.com");
            assertThat(certificated.getNickname()).isEqualTo("Sandro");
            assertThat(certificated.getAddress()).isEqualTo("Seoul");
            assertThat(certificated.getCertificationCode()).isEqualTo("1234");
            assertThat(certificated.getLastLoginAt()).isEqualTo(1L);
        }

        @Test
        void failure() throws Exception {
            // Given
            User user = User.builder()
                    .id(1L)
                    .email("test@gmail.com")
                    .nickname("Sandro")
                    .address("Seoul")
                    .status(UserStatus.PENDING)
                    .certificationCode("1234")
                    .lastLoginAt(1L)
                    .build();
            assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING);

            // When & Then
            assertThatThrownBy(() -> user.certificate("4321"))
                    .isInstanceOf(CertificationCodeNotMatchedException.class)
                    .hasMessage("자격 증명에 실패하였습니다.");
        }
    }

}
