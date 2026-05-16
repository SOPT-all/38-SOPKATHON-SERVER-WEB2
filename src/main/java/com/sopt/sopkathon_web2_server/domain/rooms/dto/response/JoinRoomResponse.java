package com.sopt.sopkathon_web2_server.domain.rooms.dto.response;

import com.sopt.sopkathon_web2_server.domain.participants.entity.ParticipantRole;

public record JoinRoomResponse(
        Long roomId,
        Long participantId,
        ParticipantRole role,
        String browserToken
) {
}
