package com.sopt.sopkathon_web2_server.domain.rooms.dto.response;

import com.sopt.sopkathon_web2_server.domain.participants.entity.ParticipantRole;
import io.swagger.v3.oas.annotations.media.Schema;

public record CreateRoomResponse(
        @Schema(description = "방 ID", example = "3")
        Long roomId,
        @Schema(description = "초대 토큰", example = "b9da634b2123")
        String inviteToken,
        @Schema(description = "초대 URL", example = "http://localhost:5173/invite/b9da634b2123")
        String inviteUrl,
        @Schema(description = "생성된 참여자 ID", example = "4")
        Long participantId,
        @Schema(description = "생성된 참여자 역할", example = "CHILD")
        ParticipantRole role,
        @Schema(description = "브라우저 토큰", example = "b31390e4e3fe36fddbd05033b3068e01")
        String browserToken
) {
}
