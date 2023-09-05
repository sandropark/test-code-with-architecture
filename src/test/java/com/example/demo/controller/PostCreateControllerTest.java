package com.example.demo.controller;

import com.example.demo.model.dto.PostCreateDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(value = "/sql/post-create-test-data.sql")
class PostCreateControllerTest extends ControllerTestSupport {

    @Test
    void create() throws Exception {
        PostCreateDto postCreateDto = PostCreateDto.builder()
                .content("안녕")
                .writerId(1)
                .build();

        mvc.perform(post("/api/posts")
                        .content(objectMapper.writeValueAsBytes(postCreateDto))
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