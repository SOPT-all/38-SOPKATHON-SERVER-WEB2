package com.sopt.sopkathon_web2_server.domain.rooms.controller;

import com.sopt.sopkathon_web2_server.domain.rooms.dto.request.JoinRoomRequest;
import com.sopt.sopkathon_web2_server.domain.rooms.dto.request.SwapRoomRolesRequest;
import com.sopt.sopkathon_web2_server.domain.rooms.dto.response.CreateRoomResponse;
import com.sopt.sopkathon_web2_server.domain.rooms.dto.response.JoinRoomResponse;
import com.sopt.sopkathon_web2_server.domain.rooms.dto.response.SwapRoomRolesResponse;
import com.sopt.sopkathon_web2_server.domain.rooms.service.RoomService;
import com.sopt.sopkathon_web2_server.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ApiResponse<CreateRoomResponse> createRoom() {
        return ApiResponse.created(roomService.createRoom());
    }

    @PostMapping("/join")
    public ApiResponse<JoinRoomResponse> joinRoom(
            @RequestBody JoinRoomRequest request
    ) {
        return ApiResponse.ok(roomService.joinRoom(request.inviteToken()));
    }

    @PatchMapping("/{roomId}/roles/swap")
    public ApiResponse<SwapRoomRolesResponse> swapRoles(
            @PathVariable Long roomId,
            @RequestBody SwapRoomRolesRequest request
    ) {
        return ApiResponse.ok(roomService.swapRoles(roomId, request.browserToken()));
    }
}
