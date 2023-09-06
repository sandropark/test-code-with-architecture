package com.example.demo.small.user.service;

import com.example.demo.mock.FakeMailSender;
import com.example.demo.user.service.CertificationService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CertificationServiceTest {

    @Test
    void send() throws Exception {
        // Given
        FakeMailSender mailSender = new FakeMailSender();
        CertificationService certificationService = new CertificationService(mailSender);
        String email = "test@gmail.com";
        long userId = 1L;
        String certificationCode = "1234";
        String content = "Please click the following link to certify your email address: http://localhost:8080/api/users/" + userId + "/verify?certificationCode=" + certificationCode;

        // When
        certificationService.send(email, userId, certificationCode);

        // Then
        assertThat(mailSender.getEmail()).isEqualTo(email);
        assertThat(mailSender.getTitle()).isEqualTo("Please certify your email address");
        assertThat(mailSender.getContent()).isEqualTo(content);
    }

}