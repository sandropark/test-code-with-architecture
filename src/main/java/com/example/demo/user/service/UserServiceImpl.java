package com.example.demo.user.service;

import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.common.service.port.ClockHolder;
import com.example.demo.common.service.port.UuidHolder;
import com.example.demo.user.controller.port.*;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.service.port.UserRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Builder
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserCreateService, UserUpdateService, UserReadService, AuthenticationService, UserService {

    private final UserRepository userRepository;
    private final CertificationService certificationService;
    private final UuidHolder uuidHolder;
    private final ClockHolder clockHolder;

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Users", email));
    }

    @Override
    public User getById(Long id) {
        return userRepository.findByIdAndStatus(id, UserStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Users", id));
    }

    @Transactional
    @Override
    public User create(UserCreate userCreate) {
        User user = User.create(userCreate, uuidHolder);
        User saved = userRepository.save(user);
        certificationService.send(saved.getEmail(), saved.getId(), saved.getCertificationCode());
        return saved;
    }

    @Transactional
    @Override
    public User update(Long id, UserUpdate userUpdate) {
        User user = getById(id);
        User updatedUser = user.update(userUpdate);
        return userRepository.save(updatedUser);
    }

    @Transactional
    @Override
    public User update(User user, UserUpdate userUpdate) {
        User updatedUser = user.update(userUpdate);
        return userRepository.save(updatedUser);
    }

    @Transactional
    @Override
    public User login(Long id) {
        User user = getById(id);
        User logined = user.login(clockHolder);
        return userRepository.save(logined);
    }

    @Transactional
    @Override
    public User login(User user) {
        User logined = user.login(clockHolder);
        return userRepository.save(logined);
    }

    @Transactional
    public void verifyEmail(Long id, String certificationCode) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Users", id));
        User certificated = user.certificate(certificationCode);
        userRepository.save(certificated);
    }

}