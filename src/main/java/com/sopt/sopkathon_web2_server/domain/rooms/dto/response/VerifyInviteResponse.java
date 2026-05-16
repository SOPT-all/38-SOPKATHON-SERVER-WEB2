package com.sopt.sopkathon_web2_server.domain.rooms.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record VerifyInviteResponse(
        @Schema(description = "방 ID", example = "3")
        Long roomId,
        @Schema(description = "참여자 ID", example = "5")
        Long participantId,
        @Schema(description = "참여 순서", example = "2")
        Integer participantOrder,
        @Schema(description = "브라우저 토큰", example = "0f2d0ac39d97fb9424d5401ccf73d8f9")
        String browserToken,
        @Schema(description = "새 참여자 생성 여부", example = "true")
        Boolean isNewParticipant,
        @Schema(description = "현재 참여자 수", example = "2")
        Integer joinedParticipantCount
) {
}
