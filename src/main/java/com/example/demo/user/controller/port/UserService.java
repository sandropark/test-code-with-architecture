package com.example.demo.user.controller.port;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserUpdate;

public interface UserService {
    User getById(Long id);

    void verifyEmail(Long id, String certificationCode);

    User getByEmail(String email);

    User login(Long id);

    User login(User user);

    User create(UserCreate userCreate);

    User update(Long id, UserUpdate userUpdate);

    User update(User user, UserUpdate userUpdate);
}
