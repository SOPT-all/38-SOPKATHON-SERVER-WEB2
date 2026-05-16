package com.sopt.sopkathon_web2_server.domain.rooms.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "방 참여자 역할 교체 응답")
public record SwapRoomRolesResponse(
        @Schema(description = "역할이 교체된 방 ID", example = "1")
        Long roomId,
        @Schema(description = "역할 교체 후 참여자 목록")
        List<ParticipantRoleResponse> participants
) {
}
