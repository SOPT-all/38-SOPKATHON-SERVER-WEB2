package com.sopt.sopkathon_web2_server.domain.rooms.dto.response;

import com.sopt.sopkathon_web2_server.domain.participants.entity.ParticipantRole;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "방 참여 응답")
public record JoinRoomResponse(
        @Schema(description = "참여한 방 ID", example = "1")
        Long roomId,
        @Schema(description = "생성된 참여자 ID", example = "2")
        Long participantId,
        @Schema(description = "참여자의 역할", example = "PARENT")
        ParticipantRole role,
        @Schema(description = "이 브라우저를 식별하는 토큰", example = "fedcba9876543210fedcba9876543210")
        String browserToken
) {
}
