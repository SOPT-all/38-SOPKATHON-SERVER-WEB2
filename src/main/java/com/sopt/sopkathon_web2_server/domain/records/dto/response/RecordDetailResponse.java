// 완료된 질문-답변 기록의 상세 정보를 담는 응답 DTO
package com.sopt.sopkathon_web2_server.domain.records.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record RecordDetailResponse(
        @Schema(description = "질문-답변 기록 ID", example = "1")
        Long roomQuestionId,
        @Schema(description = "질문 내용", example = "어릴 때 꿈이 뭐였어?")
        String question,
        @Schema(description = "양쪽 모두 답변 완료 시간", example = "2026-05-17T20:24:00")
        LocalDateTime completedAt,
        @Schema(description = "답변 영상 목록")
        List<RecordAnswerResponse> answers
) {
}
