package com.example.demo.user.service;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.infrastructure.UserEntity;
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
            UserEntity userEntity = userService.getByEmail("active@gmail.com");

            assertThat(userEntity.getStatus()).isEqualTo(UserStatus.ACTIVE);
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
            UserEntity userEntity = userService.getById(2);

            assertThat(userEntity.getStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        void failure() throws Exception {
            assertThatThrownBy(() -> userService.getById(3))
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
        UserEntity userEntity = userService.create(userCreate);

        // Then
        assertThat(userEntity.getId()).isNotNull();
        assertThat(userEntity.getStatus()).isEqualTo(UserStatus.PENDING);
//        assertThat(userEntity.getCertificationCode()).isEqualTo("");  // FIXME
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
        UserEntity userEntity = userService.update(2, userUpdate);

        // Then
        assertThat(userEntity.getAddress()).isEqualTo(address);
        assertThat(userEntity.getNickname()).isEqualTo(nickname);
    }

    @Test
    void login() throws Exception {
        // Given
        // When
        userService.login(2);

        // Then
        UserEntity userEntity = userService.getById(2);
        assertThat(userEntity.getLastLoginAt()).isGreaterThan(0);
//        assertThat(userEntity.getLastLoginAt()).isGreaterThan(""); // FIXME
    }

    @Nested
    class VerifyEmail {
        @Test
        void success() throws Exception {
            // When
            userService.verifyEmail(3, "1235");

            // Then
            UserEntity userEntity = userService.getById(3);
            assertThat(userEntity.getStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        void failure() throws Exception {
            assertThatThrownBy(() -> userService.verifyEmail(3, "123"))
                    .isInstanceOf(CertificationCodeNotMatchedException.class);
        }
    }

}