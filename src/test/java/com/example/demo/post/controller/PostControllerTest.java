package com.example.demo.post.controller;

import com.example.demo.common.domain.MyControllerTest;
import com.example.demo.post.domain.PostUpdate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SqlGroup({
        @Sql(value = "/sql/post-controller-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
@MyControllerTest
class PostControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;

    @Nested
    class GetById {
        @Test
        void success() throws Exception {
            mvc.perform(get("/api/posts/1"))
                    .andDo(print())
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.id").value(1),
                            jsonPath("$.content").value("content"),
                            jsonPath("$.writer.id").value(2),
                            jsonPath("$.writer.email").value("active@gmail.com"),
                            jsonPath("$.writer.nickname").value("Sandro")
                    );
        }

        @Test
        void failure() throws Exception {
            mvc.perform(get("/api/posts/1234"))
                    .andDo(print())
                    .andExpectAll(
                            status().isNotFound(),
                            content().string("Posts에서 ID 1234를 찾을 수 없습니다.")
                    );
        }
    }

    @Test
    void updatePost() throws Exception {
        PostUpdate postUpdate = PostUpdate.builder()
                .content("post update")
                .build();

        mvc.perform(put("/api/posts/1")
                        .content(objectMapper.writeValueAsBytes(postUpdate))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.content").value("post update"),
                        jsonPath("$.writer.id").value(2),
                        jsonPath("$.writer.email").value("active@gmail.com"),
                        jsonPath("$.writer.nickname").value("Sandro")
                );
    }
}