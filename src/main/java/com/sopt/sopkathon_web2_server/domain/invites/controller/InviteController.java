package com.sopt.sopkathon_web2_server.domain.invites.controller;

import com.sopt.sopkathon_web2_server.domain.invites.dto.request.VerifyInviteRequest;
import com.sopt.sopkathon_web2_server.domain.rooms.dto.response.VerifyInviteResponse;
import com.sopt.sopkathon_web2_server.domain.rooms.service.RoomService;
import com.sopt.sopkathon_web2_server.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invites")
public class InviteController {

    private final RoomService roomService;

    @PostMapping("/verify")
    public ApiResponse<VerifyInviteResponse> verifyInvite(
            @RequestBody VerifyInviteRequest request
    ) {
        return ApiResponse.ok(roomService.verifyInvite(request.inviteToken()));
    }
}
