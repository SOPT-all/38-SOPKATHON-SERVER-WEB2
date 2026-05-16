package com.sopt.sopkathon_web2_server.domain.invites.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record VerifyInviteRequest(
        @Schema(description = "초대 토큰", example = "b9da634b2123")
        String inviteToken
) {
}
