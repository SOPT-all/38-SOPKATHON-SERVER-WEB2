package com.sopt.sopkathon_web2_server.domain.rooms.controller;

import com.sopt.sopkathon_web2_server.domain.rooms.dto.request.JoinRoomRequest;
import com.sopt.sopkathon_web2_server.domain.rooms.dto.request.SwapRoomRolesRequest;
import com.sopt.sopkathon_web2_server.domain.rooms.dto.response.CreateRoomResponse;
import com.sopt.sopkathon_web2_server.domain.rooms.dto.response.JoinRoomResponse;
import com.sopt.sopkathon_web2_server.domain.rooms.dto.response.SwapRoomRolesResponse;
import com.sopt.sopkathon_web2_server.domain.rooms.service.RoomService;
import com.sopt.sopkathon_web2_server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Rooms", description = "방 생성, 참여, 역할 교체 API")
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "방 생성", description = "초대 링크와 첫 참여자의 브라우저 토큰을 발급합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "방 생성 성공")
    })
    @PostMapping
    public ApiResponse<CreateRoomResponse> createRoom() {
        return ApiResponse.created(roomService.createRoom());
    }

    @Operation(summary = "방 참여", description = "초대 토큰으로 방에 보호자 참여자를 등록합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "방 참여 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 초대 토큰")
    })
    @PostMapping("/join")
    public ApiResponse<JoinRoomResponse> joinRoom(
            @RequestBody JoinRoomRequest request
    ) {
        return ApiResponse.ok(roomService.joinRoom(request.inviteToken()));
    }

    @Operation(summary = "방 참여자 역할 교체", description = "방 안의 아이와 보호자 역할을 서로 교체합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "역할 교체 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "역할 교체 불가")
    })
    @PatchMapping("/{roomId}/roles/swap")
    public ApiResponse<SwapRoomRolesResponse> swapRoles(
            @PathVariable Long roomId,
            @RequestBody SwapRoomRolesRequest request
    ) {
        return ApiResponse.ok(roomService.swapRoles(roomId, request.browserToken()));
    }
}
