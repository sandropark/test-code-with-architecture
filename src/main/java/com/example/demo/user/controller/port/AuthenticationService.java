package com.example.demo.user.controller.port;

import com.example.demo.user.domain.User;

public interface AuthenticationService {
    void verifyEmail(Long id, String certificationCode);

    User login(User user);
}
