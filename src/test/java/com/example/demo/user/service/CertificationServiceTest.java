package com.example.demo.user.service;

import com.example.demo.mock.FakeMailSender;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CertificationServiceTest {

    @Test
    void send() throws Exception {
        // Given
        FakeMailSender mailSender = new FakeMailSender();
        CertificationService certificationService = new CertificationService(mailSender);

        // When
        certificationService.send("test@gmail.com", 1L, "1234");

        // Then
        assertThat(mailSender.getEmail()).isEqualTo("test@gmail.com");
        assertThat(mailSender.getTitle()).isEqualTo("Please certify your email address");
        assertThat(mailSender.getContent()).isEqualTo("Please click the following link to certify your email address: http://localhost:8080/api/users/1/verify?certificationCode=1234");
    }

}