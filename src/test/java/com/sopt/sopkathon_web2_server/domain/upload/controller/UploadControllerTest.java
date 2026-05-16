package com.sopt.sopkathon_web2_server.domain.upload.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createPresignedUrlRejectsMissingAuthorizationHeader() throws Exception {
        mockMvc.perform(post("/api/uploads/presigned-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fileName\":\"answer.png\",\"contentType\":\"image/png\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error.code").value(40003))
                .andExpect(jsonPath("$.error.message").value("브라우저 토큰이 올바르지 않습니다."));
    }
}
