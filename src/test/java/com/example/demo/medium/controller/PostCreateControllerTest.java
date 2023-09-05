package com.example.demo.medium.controller;

import com.example.demo.medium.MyControllerTest;
import com.example.demo.post.domain.PostCreate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(value = "/sql/post-create-test-data.sql")
@MyControllerTest
class PostCreateControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void create() throws Exception {
        PostCreate postCreate = PostCreate.builder()
                .content("안녕")
                .writerId(1)
                .build();

        mvc.perform(post("/api/posts")
                        .content(objectMapper.writeValueAsBytes(postCreate))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.content").value("안녕"),
                        jsonPath("$.writer.id").value(1),
                        jsonPath("$.writer.email").value("active@gmail.com"),
                        jsonPath("$.writer.nickname").value("Sandro")
                );
    }
}