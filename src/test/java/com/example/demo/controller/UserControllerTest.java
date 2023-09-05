package com.example.demo.controller;

import com.example.demo.model.UserStatus;
import com.example.demo.model.dto.UserUpdateDto;
import com.example.demo.repository.UserEntity;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SqlGroup({
        @Sql(value = "/sql/user-controller-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class UserControllerTest extends ControllerTestSupport {

    @Nested
    class GetById {
        @Test
        void success() throws Exception {
            mvc.perform(get("/api/users/1"))
                    .andDo(print())
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.id").value(1),
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
            mvc.perform(get("/api/users/2/verify")
                            .queryParam("certificationCode", "1235")
                    )
                    .andDo(print())
                    .andExpectAll(status().isFound());

            UserEntity userEntity = userRepository.findById(2L).orElseThrow();
            assertThat(userEntity.getStatus()).isEqualTo(UserStatus.ACTIVE);
        }
    }

    @Test
    void getMyInfo() throws Exception {
        mvc.perform(get("/api/users/me")
                        .header("EMAIL", "active@gmail.com"))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.email").value("active@gmail.com"),
                        jsonPath("$.nickname").value("Sandro"),
                        jsonPath("$.status").value(UserStatus.ACTIVE.name()),
                        jsonPath("$.address").value("Seoul")
                );
    }

    @Test
    void updateMyInfo() throws Exception {
        UserUpdateDto updateDto = UserUpdateDto.builder()
                .address("Busan")
                .nickname("SandroKing")
                .build();

        mvc.perform(put("/api/users/me")
                        .header("EMAIL", "active@gmail.com")
                        .content(objectMapper.writeValueAsBytes(updateDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(1),
                        jsonPath("$.email").value("active@gmail.com"),
                        jsonPath("$.nickname").value("SandroKing"),
                        jsonPath("$.status").value(UserStatus.ACTIVE.name()),
                        jsonPath("$.address").value("Busan")
                );
    }

}