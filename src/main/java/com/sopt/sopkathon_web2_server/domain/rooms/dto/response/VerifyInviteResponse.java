package com.sopt.sopkathon_web2_server.domain.rooms.dto.response;

public record VerifyInviteResponse(
        Long roomId,
        Long participantId,
        Integer participantOrder,
        String browserToken,
        Boolean isNewParticipant,
        Integer joinedParticipantCount
) {
}
