package com.sopt.sopkathon_web2_server.domain.rooms.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record SwapRoomRolesRequest(
        @Schema(description = "역할 변경을 요청하는 참여자의 브라우저 토큰", example = "b31390e4e3fe36fddbd05033b3068e01")
        String browserToken
) {
}
