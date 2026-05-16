package com.sopt.sopkathon_web2_server.domain.rooms.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "방 참여 요청")
public record JoinRoomRequest(
        @Schema(description = "방 참여에 사용하는 초대 토큰", example = "a1b2c3d4e5f6")
        String inviteToken
) {
}
