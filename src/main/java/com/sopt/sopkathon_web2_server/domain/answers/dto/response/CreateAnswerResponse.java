package com.sopt.sopkathon_web2_server.domain.answers.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "답변 등록 응답")
public record CreateAnswerResponse(
        @Schema(description = "생성된 답변 ID", example = "1")
        Long answerId,
        @Schema(description = "답변한 방 질문 ID", example = "1")
        Long roomQuestionId,
        @Schema(description = "등록된 답변 영상의 S3 키", example = "uploads/answer.mp4")
        String videoKey,
        @JsonProperty("isUnlocked")
        @Schema(description = "상대 답변 확인 가능 여부", example = "false")
        Boolean isUnlocked,
        @Schema(description = "현재 연속 답변 일수", example = "6")
        Integer currentStreakDay,
        @Schema(description = "현재 연속 답변 상태 메시지", example = "드디어 맞닿았어요!")
        String currentStreakMessage,
        @Schema(description = "답변 생성 시각", example = "2026-05-17T05:30:00")
        LocalDateTime createdAt
) {
}
