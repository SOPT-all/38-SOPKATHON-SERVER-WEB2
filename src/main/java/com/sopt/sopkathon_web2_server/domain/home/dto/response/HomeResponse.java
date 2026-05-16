package com.sopt.sopkathon_web2_server.domain.home.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "홈 화면 응답")
public record HomeResponse(
        @Schema(description = "현재 사용자의 선택 모드", example = "CHILD")
        String selectedMode,
        @Schema(description = "홈 화면 상태 메시지", example = "답장을 받지 못해 멀어지는 중이에요..")
        String statusMessage,
        @Schema(description = "부모님 답변 상태 메시지", example = "부모님 답변은 아직이에요")
        String parentAnswerStatusMessage,
        @Schema(description = "진행도 정보")
        HomeProgressResponse progress,
        @Schema(description = "오늘의 질문 정보")
        TodayQuestionResponse todayQuestion
) {
}
