package com.example.demo.small.user.controller.request;

import com.example.demo.user.controller.request.UserUpdateRequest;
import com.example.demo.user.domain.UserUpdate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserUpdateRequestTest {

    @Test
    void toModel() throws Exception {
        // Given
        UserUpdateRequest userUpdateRequest = UserUpdateRequest.builder()
                .address("서울")
                .nickname("두리")
                .build();

        // When
        UserUpdate userUpdate = userUpdateRequest.toModel();

        // Then
        assertThat(userUpdate.getAddress()).isEqualTo(userUpdateRequest.getAddress());
        assertThat(userUpdate.getNickname()).isEqualTo(userUpdateRequest.getNickname());
    }

}