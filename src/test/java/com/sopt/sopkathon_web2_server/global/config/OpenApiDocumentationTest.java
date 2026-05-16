// Swagger API 문서 메타데이터를 검증하는 테스트
package com.sopt.sopkathon_web2_server.global.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OpenApiDocumentationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void openApiDocsExposeSwaggerMetadataForCallableApis() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.paths['/api/rooms'].post.tags[0]").value("Rooms"))
                .andExpect(jsonPath("$.paths['/api/rooms'].post.summary").value("방 생성"))
                .andExpect(jsonPath("$.paths['/api/rooms/join'].post.summary").value("방 참여"))
                .andExpect(jsonPath("$.paths['/api/rooms/{roomId}/roles/swap'].patch.summary").value("방 참여자 역할 교체"))
                .andExpect(jsonPath("$.paths['/api/invites/verify'].post.tags[0]").value("Invites"))
                .andExpect(jsonPath("$.paths['/api/invites/verify'].post.summary").value("초대 토큰 검증"))
                .andExpect(jsonPath("$.paths['/api/answers'].post.tags[0]").value("Answers"))
                .andExpect(jsonPath("$.paths['/api/answers'].post.summary").value("답변 등록"))
                .andExpect(jsonPath("$.paths['/api/answers'].post.parameters[0].name").value("Authorization"))
                .andExpect(jsonPath("$.paths['/api/answers'].post.parameters[0].description").value("Bearer 브라우저 토큰"))
                .andExpect(jsonPath("$.paths['/api/uploads/presigned-url'].post.tags[0]").value("Uploads"))
                .andExpect(jsonPath("$.paths['/api/uploads/presigned-url'].post.summary").value("Presigned URL 발급"))
                .andExpect(jsonPath("$.paths['/api/home'].get.tags[0]").value("Home"))
                .andExpect(jsonPath("$.paths['/api/home'].get.summary").value("홈 화면 조회"))
                .andExpect(jsonPath("$.components.schemas.JoinRoomRequest.properties.inviteToken.description").value("방 참여에 사용하는 초대 토큰"))
                .andExpect(jsonPath("$.components.schemas.CreateAnswerRequest.properties.videoKey.example").value("uploads/answer.mp4"))
                .andExpect(jsonPath("$.components.schemas.HomeResponse.properties.todayQuestion.description").value("오늘의 질문 정보"));
    }
}
