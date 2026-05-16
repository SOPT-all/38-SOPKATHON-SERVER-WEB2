package com.sopt.sopkathon_web2_server.domain.rooms.dto.response;

import com.sopt.sopkathon_web2_server.domain.participants.entity.ParticipantRole;
import io.swagger.v3.oas.annotations.media.Schema;

public record JoinRoomResponse(
        @Schema(description = "방 ID", example = "3")
        Long roomId,
        @Schema(description = "참여자 ID", example = "5")
        Long participantId,
        @Schema(description = "참여자 역할", example = "PARENT")
        ParticipantRole role,
        @Schema(description = "브라우저 토큰", example = "0f2d0ac39d97fb9424d5401ccf73d8f9")
        String browserToken
) {
}
