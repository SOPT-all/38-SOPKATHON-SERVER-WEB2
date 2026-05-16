package com.sopt.sopkathon_web2_server.domain.home.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "홈 화면 진행도 정보")
public record HomeProgressResponse(
        @Schema(description = "현재 진행 단계", example = "1")
        Integer currentStep,
        @Schema(description = "전체 진행 단계", example = "4")
        Integer totalStep,
        @Schema(description = "진행 단계 메시지", example = "아직은 머뭇거리는 중이에요.")
        String message
) {
}
