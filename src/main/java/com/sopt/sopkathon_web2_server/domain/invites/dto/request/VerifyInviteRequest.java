package com.sopt.sopkathon_web2_server.domain.invites.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "초대 토큰 검증 요청")
public record VerifyInviteRequest(
        @Schema(description = "검증할 초대 토큰", example = "a1b2c3d4e5f6")
        String inviteToken
) {
}
