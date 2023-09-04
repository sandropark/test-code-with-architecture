package com.example.demo;

import com.example.demo.model.UserStatus;
import com.example.demo.repository.PostEntity;
import com.example.demo.repository.PostRepository;
import com.example.demo.repository.UserEntity;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class TestContext {

    public static final long ACTIVE_ID = 1L;
    public static final long INACTIVE_ID = 2L;
    public static final long PENDING_ID = 3L;
    public static final String ACTIVE_EMAIL = "active@gmail.com";
    public static final String INACTIVE_EMAIL = "inactive@gmail.com";
    public static final String PENDING_EMAIL = "pendig@gmail.com";
    public static final String CERTIFICATION_CODE = "123123123123";
    public static final long POST_ID = 1L;

    @Autowired
    UserRepository userRepository;
    @Autowired
    PostRepository postRepository;

    @EventListener(ApplicationReadyEvent.class)
    void setUp() {
        UserEntity activeUser = getActiveUserEntity();
        userRepository.save(activeUser);
        userRepository.save(getInActiveUserEntity());
        userRepository.save(getPendigUserEntity());
        postRepository.save(getPostEntity(activeUser));
    }

    private PostEntity getPostEntity(UserEntity activeUser) {
        PostEntity postEntity = new PostEntity();
        postEntity.setId(POST_ID);
        postEntity.setContent("content");
        postEntity.setWriter(activeUser);
        return postEntity;
    }

    private UserEntity getActiveUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(ACTIVE_ID);
        userEntity.setEmail(ACTIVE_EMAIL);
        userEntity.setAddress("Seoul");
        userEntity.setNickname("Sandro");
        userEntity.setStatus(UserStatus.ACTIVE);
        userEntity.setCertificationCode("123123123123");
        return userEntity;
    }

    private UserEntity getInActiveUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(INACTIVE_ID);
        userEntity.setEmail(INACTIVE_EMAIL);
        userEntity.setAddress("Seoul");
        userEntity.setNickname("Sandro");
        userEntity.setStatus(UserStatus.INACTIVE);
        userEntity.setCertificationCode("123123123123");
        return userEntity;
    }

    private UserEntity getPendigUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(PENDING_ID);
        userEntity.setEmail(PENDING_EMAIL);
        userEntity.setAddress("Seoul");
        userEntity.setNickname("Sandro");
        userEntity.setStatus(UserStatus.PENDING);
        userEntity.setCertificationCode(CERTIFICATION_CODE);
        return userEntity;
    }

}
