package com.sopt.sopkathon_web2_server.domain.rooms.dto.response;

import com.sopt.sopkathon_web2_server.domain.participants.entity.ParticipantRole;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "참여자 역할 정보")
public record ParticipantRoleResponse(
        @Schema(description = "참여자 ID", example = "1")
        Long participantId,
        @Schema(description = "참여자의 현재 역할", example = "CHILD")
        ParticipantRole role
) {
}
