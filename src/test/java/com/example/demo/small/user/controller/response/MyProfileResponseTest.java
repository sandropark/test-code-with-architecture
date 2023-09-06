package com.example.demo.small.user.controller.response;

import com.example.demo.user.controller.response.MyProfileResponse;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MyProfileResponseTest {

    @Test
    void fromModel() throws Exception {
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
        MyProfileResponse myProfileResponse = MyProfileResponse.fromModel(user);

        // Then
        assertThat(myProfileResponse.getEmail()).isEqualTo("test@gmail.com");
        assertThat(myProfileResponse.getNickname()).isEqualTo("Sandro");
        assertThat(myProfileResponse.getAddress()).isEqualTo("Seoul");
        assertThat(myProfileResponse.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(myProfileResponse.getLastLoginAt()).isEqualTo(1L);
    }

}