package com.example.demo.mock;

import com.example.demo.mock.infrastructure.FakeMailSender;
import com.example.demo.mock.infrastructure.FakePostRepository;
import com.example.demo.mock.infrastructure.FakeUserRepository;
import com.example.demo.post.controller.PostController;
import com.example.demo.post.controller.PostCreateController;
import com.example.demo.post.controller.port.PostService;
import com.example.demo.post.service.PostServiceImpl;
import com.example.demo.post.service.port.PostRepository;
import com.example.demo.user.controller.UserController;
import com.example.demo.user.controller.UserCreateController;
import com.example.demo.user.controller.port.UserService;
import com.example.demo.user.service.CertificationService;
import com.example.demo.user.service.UserServiceImpl;
import com.example.demo.user.service.port.MailSender;
import com.example.demo.user.service.port.UserRepository;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TestContainer {

    private final MailSender mailSender;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CertificationService certificationService;
    private final UserService userService;
    private final PostService postService;
    private final UserController userController;
    private final UserCreateController userCreateController;
    private final PostController postController;
    private final PostCreateController postCreateController;

    @Builder
    private TestContainer(String certificationCode, long millis) {
        mailSender = new FakeMailSender();
        userRepository = new FakeUserRepository();
        postRepository = new FakePostRepository();
        certificationService = new CertificationService(mailSender);
        userService = UserServiceImpl.builder()
                .userRepository(userRepository)
                .certificationService(certificationService)
                .uuidHolder(() -> certificationCode)
                .clockHolder(() -> millis)
                .build();
        postService = PostServiceImpl.builder()
                .postRepository(postRepository)
                .userRepository(userRepository)
                .clockHolder(() -> millis)
                .build();
        userController = UserController.builder().userService(userService).build();
        userCreateController = new UserCreateController(userService);
        postController = new PostController(postService);
        postCreateController = new PostCreateController(postService);
    }

}