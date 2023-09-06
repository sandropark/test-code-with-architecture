package com.example.demo.user.controller.port;

import com.example.demo.user.domain.User;

public interface UserReadService {
    User getById(Long id);

    User getByEmail(String email);
}
