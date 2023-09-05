package com.example.demo.controller;

import com.example.demo.model.dto.UserCreateDto;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserCreateControllerTest extends ControllerTestSupport {
    @MockBean
    JavaMailSender javaMailSender;

    @Test
    void create() throws Exception {
        UserCreateDto createDto = UserCreateDto.builder()
                .nickname("Sandro")
                .email("create@gmail.com")
                .address("Seoul")
                .build();
        BDDMockito.doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        mvc.perform(post("/api/users")
                        .content(objectMapper.writeValueAsBytes(createDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id").isNumber(),
                        jsonPath("$.email").value("create@gmail.com"),
                        jsonPath("$.nickname").value("Sandro"),
                        jsonPath("$.status").value("PENDING")
                );
    }
}