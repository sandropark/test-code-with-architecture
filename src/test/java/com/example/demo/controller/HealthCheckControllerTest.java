package com.example.demo.controller;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HealthCheckControllerTest extends ControllerTestSupport {

    @Test
    void healthCheck() throws Exception {
        mvc.perform(get("/health_check.html"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}