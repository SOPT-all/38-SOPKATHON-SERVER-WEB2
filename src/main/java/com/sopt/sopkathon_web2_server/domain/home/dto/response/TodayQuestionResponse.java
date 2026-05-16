package com.sopt.sopkathon_web2_server.domain.home.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "오늘의 질문 정보")
public record TodayQuestionResponse(
        @Schema(description = "방 질문 ID", example = "1")
        Long roomQuestionId,
        @Schema(description = "오늘 답변할 질문 내용", example = "가장 행복했던 순간은 언제인가요?")
        String content,
        @Schema(description = "현재 참여자가 답변했는지 여부", example = "false")
        Boolean answered
) {
}
