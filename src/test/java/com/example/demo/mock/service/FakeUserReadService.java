package com.example.demo.mock.service;

import com.example.demo.user.controller.port.UserReadService;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;

public class FakeUserReadService implements UserReadService {
    @Override
    public User getById(Long id) {
        return User.builder()
                .id(1L)
                .email("test@gmail.com")
                .nickname("Sandro")
                .address("Seoul")
                .certificationCode("1234")
                .status(UserStatus.PENDING)
                .lastLoginAt(1L)
                .build();
    }

    @Override
    public User getByEmail(String email) {
        return User.builder()
                .id(1L)
                .email("test@gmail.com")
                .nickname("Sandro")
                .address("Seoul")
                .certificationCode("1234")
                .status(UserStatus.PENDING)
                .lastLoginAt(1L)
                .build();
    }
}
