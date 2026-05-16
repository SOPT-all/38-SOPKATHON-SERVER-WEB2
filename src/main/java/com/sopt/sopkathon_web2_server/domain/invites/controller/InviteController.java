package com.sopt.sopkathon_web2_server.domain.invites.controller;

import com.sopt.sopkathon_web2_server.domain.invites.dto.request.VerifyInviteRequest;
import com.sopt.sopkathon_web2_server.domain.rooms.dto.response.VerifyInviteResponse;
import com.sopt.sopkathon_web2_server.domain.rooms.service.RoomService;
import com.sopt.sopkathon_web2_server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invites")
@Tag(name = "Invites", description = "초대 토큰 검증 API")
public class InviteController {

    private final RoomService roomService;

    @Operation(summary = "초대 토큰 검증", description = "초대 링크 진입 시 초대 토큰을 검증하고 참여 정보를 반환합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "초대 토큰 검증 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 초대 토큰"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "방 정원 초과")
    })
    @PostMapping("/verify")
    public ApiResponse<VerifyInviteResponse> verifyInvite(
            @RequestBody VerifyInviteRequest request
    ) {
        return ApiResponse.ok(roomService.verifyInvite(request.inviteToken()));
    }
}
