package com.sopt.sopkathon_web2_server.domain.rooms.dto.response;

import com.sopt.sopkathon_web2_server.domain.participants.entity.ParticipantRole;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "방 생성 응답")
public record CreateRoomResponse(
        @Schema(description = "생성된 방 ID", example = "1")
        Long roomId,
        @Schema(description = "초대 링크에 포함되는 초대 토큰", example = "a1b2c3d4e5f6")
        String inviteToken,
        @Schema(description = "프론트엔드 초대 URL", example = "http://localhost:5173/invite/a1b2c3d4e5f6")
        String inviteUrl,
        @Schema(description = "생성된 첫 참여자 ID", example = "1")
        Long participantId,
        @Schema(description = "첫 참여자의 역할", example = "CHILD")
        ParticipantRole role,
        @Schema(description = "이 브라우저를 식별하는 토큰", example = "0123456789abcdef0123456789abcdef")
        String browserToken
) {
}
