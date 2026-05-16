package com.sopt.sopkathon_web2_server.domain.rooms.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "방 참여자 역할 교체 요청")
public record SwapRoomRolesRequest(
        @Schema(description = "역할 교체를 요청한 참여자의 브라우저 토큰", example = "0123456789abcdef0123456789abcdef")
        String browserToken
) {
}
