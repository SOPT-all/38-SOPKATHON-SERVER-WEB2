package com.sopt.sopkathon_web2_server.domain.rooms.dto.response;

import com.sopt.sopkathon_web2_server.domain.participants.entity.ParticipantRole;
import io.swagger.v3.oas.annotations.media.Schema;

public record ParticipantRoleResponse(
        @Schema(description = "참여자 ID", example = "4")
        Long participantId,
        @Schema(description = "참여자 역할", example = "CHILD")
        ParticipantRole role
) {
}
