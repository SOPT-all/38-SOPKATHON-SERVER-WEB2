package com.sopt.sopkathon_web2_server.domain.rooms.dto.response;

import java.util.List;

public record SwapRoomRolesResponse(
        Long roomId,
        List<ParticipantRoleResponse> participants
) {
}
