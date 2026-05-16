package com.sopt.sopkathon_web2_server.domain.rooms.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record JoinRoomRequest(
        @Schema(description = "초대 토큰", example = "b9da634b2123")
        String inviteToken
) {
}
