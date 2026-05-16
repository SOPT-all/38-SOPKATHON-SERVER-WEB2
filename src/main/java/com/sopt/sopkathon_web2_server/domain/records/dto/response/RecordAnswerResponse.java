// 기록 상세 화면에 표시할 답변 영상 정보를 담는 응답 DTO
package com.sopt.sopkathon_web2_server.domain.records.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sopt.sopkathon_web2_server.domain.participants.entity.ParticipantRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record RecordAnswerResponse(
        @Schema(description = "답변자 역할", example = "PARENT")
        ParticipantRole role,
        @Schema(description = "답변 영상 URL", example = "https://bucket.s3.ap-northeast-2.amazonaws.com/uploads/parent-answer.mp4")
        String videoUrl,
        @Schema(description = "답변 생성 시각", example = "2026-05-17T20:24:00")
        LocalDateTime answeredAt,
        @JsonProperty("isMine")
        @Schema(description = "현재 사용자의 답변 여부", example = "false")
        Boolean isMine
) {
}
