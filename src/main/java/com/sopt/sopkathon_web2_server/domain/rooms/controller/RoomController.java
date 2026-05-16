package com.sopt.sopkathon_web2_server.domain.rooms.controller;

import com.sopt.sopkathon_web2_server.domain.rooms.dto.request.JoinRoomRequest;
import com.sopt.sopkathon_web2_server.domain.rooms.dto.request.SwapRoomRolesRequest;
import com.sopt.sopkathon_web2_server.domain.rooms.dto.response.CreateRoomResponse;
import com.sopt.sopkathon_web2_server.domain.rooms.dto.response.JoinRoomResponse;
import com.sopt.sopkathon_web2_server.domain.rooms.dto.response.SwapRoomRolesResponse;
import com.sopt.sopkathon_web2_server.domain.rooms.service.RoomService;
import com.sopt.sopkathon_web2_server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Tag(name = "Room", description = "방 생성, 입장, 역할 변경 API")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @Operation(summary = "방 생성", description = "새 방을 생성하고 생성자를 CHILD 역할의 첫 참여자로 등록합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "방 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "data": {
                                        "roomId": 3,
                                        "inviteToken": "b9da634b2123",
                                        "inviteUrl": "http://localhost:5173/invite/b9da634b2123",
                                        "participantId": 4,
                                        "role": "CHILD",
                                        "browserToken": "b31390e4e3fe36fddbd05033b3068e01"
                                      },
                                      "error": null
                                    }
                                    """)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "success": false,
                                      "data": null,
                                      "error": {
                                        "code": 50000,
                                        "message": "서버 내부 오류입니다."
                                      }
                                    }
                                    """)
                    )
            )
    })
    public ApiResponse<CreateRoomResponse> createRoom() {
        return ApiResponse.created(roomService.createRoom());
    }

    @PostMapping("/join")
    @Operation(summary = "방 입장", description = "초대 토큰으로 방에 입장하고 참여자를 PARENT 역할로 등록합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "방 입장 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "data": {
                                        "roomId": 3,
                                        "participantId": 5,
                                        "role": "PARENT",
                                        "browserToken": "0f2d0ac39d97fb9424d5401ccf73d8f9"
                                      },
                                      "error": null
                                    }
                                    """)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 초대 토큰",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "INVALID_INVITE_TOKEN",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "data": null,
                                                      "error": {
                                                        "code": 40002,
                                                        "message": "초대 토큰이 올바르지 않습니다."
                                                      }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "INVALID_ROOM_PARTICIPANT",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "data": null,
                                                      "error": {
                                                        "code": 40004,
                                                        "message": "방 참여자 정보가 올바르지 않습니다."
                                                      }
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "방이 가득 참",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "success": false,
                                      "data": null,
                                      "error": {
                                        "code": 40901,
                                        "message": "이미 참여자가 모두 입장한 방입니다."
                                      }
                                    }
                                    """)
                    )
            )
    })
    public ApiResponse<JoinRoomResponse> joinRoom(
            @RequestBody JoinRoomRequest request
    ) {
        return ApiResponse.ok(roomService.joinRoom(request.inviteToken()));
    }

    @PatchMapping("/{roomId}/roles/swap")
    @Operation(summary = "역할 변경", description = "해당 방의 두 참여자 역할을 CHILD/PARENT로 서로 교체합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "역할 변경 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "data": {
                                        "roomId": 3,
                                        "participants": [
                                          {
                                            "participantId": 4,
                                            "role": "PARENT"
                                          },
                                          {
                                            "participantId": 5,
                                            "role": "CHILD"
                                          }
                                        ]
                                      },
                                      "error": null
                                    }
                                    """)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "INVALID_ROOM_PARTICIPANT",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "data": null,
                                                      "error": {
                                                        "code": 40004,
                                                        "message": "방 참여자 정보가 올바르지 않습니다."
                                                      }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "INVALID_ROLE_SWAP_REQUEST",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "data": null,
                                                      "error": {
                                                        "code": 40005,
                                                        "message": "역할을 변경할 수 없습니다."
                                                      }
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    public ApiResponse<SwapRoomRolesResponse> swapRoles(
            @PathVariable Long roomId,
            @RequestBody SwapRoomRolesRequest request
    ) {
        return ApiResponse.ok(roomService.swapRoles(roomId, request.browserToken()));
    }
}
