package com.example.demo.medium;

import com.example.demo.post.infrastructure.PostJpaRepository;
import com.example.demo.user.infrastructure.UserJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest
public class ControllerTestSupport {
    @Autowired
    MockMvc mvc;
    @Autowired
    UserJpaRepository userRepository;
    @Autowired
    PostJpaRepository postRepository;
    @Autowired
    ObjectMapper objectMapper;
}
