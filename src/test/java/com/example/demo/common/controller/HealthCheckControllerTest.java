package com.example.demo.common.controller;

import com.example.demo.common.domain.MyControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MyControllerTest
class HealthCheckControllerTest {

    @Autowired
    MockMvc mvc;

    @Test
    void healthCheck() throws Exception {
        mvc.perform(get("/health_check.html"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}