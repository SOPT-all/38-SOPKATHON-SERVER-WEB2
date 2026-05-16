package com.sopt.sopkathon_web2_server.domain.rooms.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record SwapRoomRolesResponse(
        @Schema(description = "방 ID", example = "3")
        Long roomId,
        @Schema(description = "역할이 변경된 참여자 목록")
        List<ParticipantRoleResponse> participants
) {
}
