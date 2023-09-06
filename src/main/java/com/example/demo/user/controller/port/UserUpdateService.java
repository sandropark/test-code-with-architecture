package com.example.demo.user.controller.port;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserUpdate;

public interface UserUpdateService {
    User update(Long id, UserUpdate userUpdate);

    User update(User user, UserUpdate userUpdate);
}
