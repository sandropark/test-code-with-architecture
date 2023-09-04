package com.example.demo.service;

import com.example.demo.TestContext;
import com.example.demo.exception.CertificationCodeNotMatchedException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.UserStatus;
import com.example.demo.model.dto.UserCreateDto;
import com.example.demo.model.dto.UserUpdateDto;
import com.example.demo.repository.UserEntity;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static com.example.demo.TestContext.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(classes = TestContext.class)
class UserServiceTest {

    @Autowired
    UserService userService;
    @MockBean
    JavaMailSender javaMailSender;

    @Nested
    class GetByEmail {
        @Test
        void success() throws Exception {
            UserEntity userEntity = userService.getByEmail(ACTIVE_EMAIL);

            assertThat(userEntity.getStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        void failure() throws Exception {
            assertThatThrownBy(() -> userService.getByEmail(INACTIVE_EMAIL))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    class GetById {
        @Test
        void success() throws Exception {
            UserEntity userEntity = userService.getById(ACTIVE_ID);

            assertThat(userEntity.getStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        void failure() throws Exception {
            assertThatThrownBy(() -> userService.getById(INACTIVE_ID))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Test
    void create() throws Exception {
        // Given
        UserCreateDto userCreateDto = UserCreateDto.builder()
                .email("create@gmail.com")
                .address("Seoul")
                .nickname("Sandro")
                .build();
        BDDMockito.doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        // When
        UserEntity userEntity = userService.create(userCreateDto);

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
        UserUpdateDto userUpdateDto = UserUpdateDto.builder()
                .address(address)
                .nickname(nickname)
                .build();

        // When
        UserEntity userEntity = userService.update(ACTIVE_ID, userUpdateDto);

        // Then
        assertThat(userEntity.getAddress()).isEqualTo(address);
        assertThat(userEntity.getNickname()).isEqualTo(nickname);
    }

    @Test
    void login() throws Exception {
        // Given
        // When
        userService.login(ACTIVE_ID);

        // Then
        UserEntity userEntity = userService.getById(ACTIVE_ID);
        assertThat(userEntity.getLastLoginAt()).isGreaterThan(0);
//        assertThat(userEntity.getLastLoginAt()).isGreaterThan(""); // FIXME
    }

    @Nested
    class VerifyEmail {
        @Test
        void success() throws Exception {
            // When
            userService.verifyEmail(PENDING_ID, CERTIFICATION_CODE);

            // Then
            UserEntity userEntity = userService.getById(PENDING_ID);
            assertThat(userEntity.getStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        void failure() throws Exception {
            assertThatThrownBy(() -> userService.verifyEmail(PENDING_ID, "123"))
                    .isInstanceOf(CertificationCodeNotMatchedException.class);
        }
    }

}