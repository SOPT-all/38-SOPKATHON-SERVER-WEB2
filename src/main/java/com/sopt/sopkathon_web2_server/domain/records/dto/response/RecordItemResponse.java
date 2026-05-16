package com.sopt.sopkathon_web2_server.domain.records.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record RecordItemResponse(
        @Schema(description = "방 질문 ID", example = "12")
        Long roomQuestionId,
        @Schema(description = "질문 내용", example = "오늘 가장 기억에 남는 순간은 무엇이었나요?")
        String question,
        @Schema(description = "양쪽 답변 완료 시각", example = "2026-05-17T21:30:00")
        LocalDateTime completedAt
) {
}
