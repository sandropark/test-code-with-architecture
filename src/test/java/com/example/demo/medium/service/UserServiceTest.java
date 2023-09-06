package com.example.demo.medium.service;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.user.controller.port.UserService;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@SqlGroup({
        @Sql(value = "/sql/user-service-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD),
})
@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;
    @MockBean
    JavaMailSender javaMailSender;

    @Nested
    class GetByEmail {
        @Test
        void success() throws Exception {
            User user = userService.getByEmail("active@gmail.com");

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
            User user = userService.getById(2L);

            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        void failure() throws Exception {
            assertThatThrownBy(() -> userService.getById(3L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Test
    void create() throws Exception {
        // Given
        UserCreate userCreate = UserCreate.builder()
                .email("create@gmail.com")
                .address("Seoul")
                .nickname("Sandro")
                .build();
        BDDMockito.doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        // When
        User user = userService.create(userCreate);

        // Then
        assertThat(user.getId()).isNotNull();
        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING);
//        assertThat(user.getCertificationCode()).isEqualTo("");
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
        User user = userService.update(2L, userUpdate);

        // Then
        assertThat(user.getAddress()).isEqualTo(address);
        assertThat(user.getNickname()).isEqualTo(nickname);
    }

    @Test
    void login() throws Exception {
        // Given
        // When
        userService.login(2L);

        // Then
        User user = userService.getById(2L);
        assertThat(user.getLastLoginAt()).isGreaterThan(0);
//        assertThat(user.getLastLoginAt()).isGreaterThan(""); // FIXME
    }

    @Nested
    class VerifyEmail {
        @Test
        void success() throws Exception {
            // When
            userService.verifyEmail(3L, "1235");

            // Then
            User user = userService.getById(3L);
            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        void failure() throws Exception {
            assertThatThrownBy(() -> userService.verifyEmail(3L, "123"))
                    .isInstanceOf(CertificationCodeNotMatchedException.class);
        }
    }

}