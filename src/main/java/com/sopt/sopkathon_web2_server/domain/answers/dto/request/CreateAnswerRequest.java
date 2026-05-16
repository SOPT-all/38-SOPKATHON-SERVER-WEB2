package com.sopt.sopkathon_web2_server.domain.answers.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "답변 등록 요청")
public record CreateAnswerRequest(
        @Schema(description = "답변할 방 질문 ID", example = "1")
        Long roomQuestionId,
        @Schema(description = "업로드된 답변 영상의 S3 키", example = "uploads/answer.mp4")
        String videoKey
) {
}
