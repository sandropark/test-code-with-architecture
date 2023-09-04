package com.example.demo;

import com.example.demo.model.UserStatus;
import com.example.demo.repository.UserEntity;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import static com.example.demo.repository.UserRepositoryTest.ID;
import static com.example.demo.repository.UserRepositoryTest.MAIL;

@SpringBootApplication
public class TestContext {

    @Autowired
    UserRepository userRepository;

    @EventListener(ApplicationReadyEvent.class)
    void setUp() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(ID);
        userEntity.setEmail(MAIL);
        userEntity.setAddress("Seoul");
        userEntity.setNickname("Sandro");
        userEntity.setStatus(UserStatus.ACTIVE);
        userEntity.setCertificationCode("123123123123");
        userRepository.save(userEntity);
    }
}
