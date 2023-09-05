package com.example.demo.medium.controller;

import com.example.demo.medium.MyControllerTest;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.service.port.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SqlGroup({
        @Sql(value = "/sql/user-controller-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
@MyControllerTest
class UserControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ObjectMapper objectMapper;

    @Nested
    class GetById {
        @Test
        void success() throws Exception {
            mvc.perform(get("/api/users/5"))
                    .andDo(print())
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.id").value(5),
                            jsonPath("$.email").value("active@gmail.com"),
                            jsonPath("$.nickname").value("Sandro"),
                            jsonPath("$.status").value(UserStatus.ACTIVE.name()),
                            jsonPath("$.address").doesNotExist()
                    );
        }

        @Test
        void failure() throws Exception {
            mvc.perform(get("/api/users/1234"))
                    .andDo(print())
                    .andExpectAll(
                            status().isNotFound(),
                            content().string("Users에서 ID 1234를 찾을 수 없습니다.")
                    );
        }
    }

    @Nested
    class Verify {
        @Test
        void success() throws Exception {
            mvc.perform(get("/api/users/6/verify")
                            .queryParam("certificationCode", "1235")
                    )
                    .andDo(print())
                    .andExpectAll(status().isFound());

            User user = userRepository.findById(6L).orElseThrow();
            assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        void failure() throws Exception {
            mvc.perform(get("/api/users/6/verify")
                            .queryParam("certificationCode", "3213")
                    )
                    .andDo(print())
                    .andExpectAll(
                            status().isForbidden(),
                            content().string("자격 증명에 실패하였습니다.")
                    );
        }
    }

    @Test
    void getMyInfo() throws Exception {
        mvc.perform(get("/api/users/me")
                        .header("EMAIL", "active@gmail.com"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(5),
                        jsonPath("$.email").value("active@gmail.com"),
                        jsonPath("$.nickname").value("Sandro"),
                        jsonPath("$.status").value(UserStatus.ACTIVE.name()),
                        jsonPath("$.address").value("Seoul")
                );
    }

    @Test
    void updateMyInfo() throws Exception {
        UserUpdate updateDto = UserUpdate.builder()
                .address("Busan")
                .nickname("SandroKing")
                .build();

        mvc.perform(put("/api/users/me")
                        .header("EMAIL", "test@gmail.com")
                        .content(objectMapper.writeValueAsBytes(updateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(7),
                        jsonPath("$.email").value("test@gmail.com"),
                        jsonPath("$.nickname").value("SandroKing"),
                        jsonPath("$.status").value(UserStatus.ACTIVE.name()),
                        jsonPath("$.address").value("Busan")
                );
    }

}