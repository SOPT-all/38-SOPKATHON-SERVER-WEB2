package com.sopt.sopkathon_web2_server.domain.invites.controller;

import com.sopt.sopkathon_web2_server.domain.invites.dto.request.VerifyInviteRequest;
import com.sopt.sopkathon_web2_server.domain.rooms.dto.response.VerifyInviteResponse;
import com.sopt.sopkathon_web2_server.domain.rooms.service.RoomService;
import com.sopt.sopkathon_web2_server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Tag(name = "Invite", description = "초대 링크 검증 API")
public class InviteController {

    private final RoomService roomService;

    @PostMapping("/verify")
    @Operation(summary = "초대 링크 검증", description = "초대 토큰을 검증하고 새 참여자를 방에 입장시킵니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "초대 링크 검증 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "data": {
                                        "roomId": 3,
                                        "participantId": 5,
                                        "participantOrder": 2,
                                        "browserToken": "0f2d0ac39d97fb9424d5401ccf73d8f9",
                                        "isNewParticipant": true,
                                        "joinedParticipantCount": 2
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
                            examples = @ExampleObject(value = """
                                    {
                                      "success": false,
                                      "data": null,
                                      "error": {
                                        "code": 40002,
                                        "message": "초대 토큰이 올바르지 않습니다."
                                      }
                                    }
                                    """)
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
    public ApiResponse<VerifyInviteResponse> verifyInvite(
            @RequestBody VerifyInviteRequest request
    ) {
        return ApiResponse.ok(roomService.verifyInvite(request.inviteToken()));
    }
}
