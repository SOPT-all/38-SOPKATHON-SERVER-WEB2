package com.sopt.sopkathon_web2_server.domain.rooms.dto.response;

import com.sopt.sopkathon_web2_server.domain.participants.entity.ParticipantRole;

public record ParticipantRoleResponse(
        Long participantId,
        ParticipantRole role
) {
}
