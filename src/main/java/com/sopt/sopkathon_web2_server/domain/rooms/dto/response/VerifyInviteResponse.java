package com.sopt.sopkathon_web2_server.domain.rooms.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "초대 토큰 검증 응답")
public record VerifyInviteResponse(
        @Schema(description = "초대 토큰이 가리키는 방 ID", example = "1")
        Long roomId,
        @Schema(description = "참여자 ID", example = "2")
        Long participantId,
        @Schema(description = "방 안에서의 참여 순서", example = "2")
        Integer participantOrder,
        @Schema(description = "이 브라우저를 식별하는 토큰", example = "fedcba9876543210fedcba9876543210")
        String browserToken,
        @Schema(description = "새로 참여한 사용자 여부", example = "true")
        Boolean isNewParticipant,
        @Schema(description = "현재 입장한 참여자 수", example = "2")
        Integer joinedParticipantCount
) {
}
