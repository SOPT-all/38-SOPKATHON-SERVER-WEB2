package com.sopt.sopkathon_web2_server.domain.records.controller;

import com.sopt.sopkathon_web2_server.domain.records.dto.response.RecordsResponse;
import com.sopt.sopkathon_web2_server.domain.records.service.RecordsService;
import com.sopt.sopkathon_web2_server.global.exception.CustomException;
import com.sopt.sopkathon_web2_server.global.exception.ErrorCode;
import com.sopt.sopkathon_web2_server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/records")
@Tag(name = "Record", description = "기록 조회 API")
public class RecordsController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final RecordsService recordsService;

    @GetMapping
    @Operation(summary = "기록 조회", description = "Authorization 헤더의 브라우저 토큰으로 참여자를 식별해 방의 답변 기록을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "기록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "data": {
                                        "records": [
                                          {
                                            "roomQuestionId": 12,
                                            "question": "오늘 가장 기억에 남는 순간은 무엇이었나요?",
                                            "completedAt": "2026-05-17T21:30:00"
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
                    description = "잘못된 브라우저 토큰",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                      "success": false,
                                      "data": null,
                                      "error": {
                                        "code": 40004,
                                        "message": "방 참여자 정보가 올바르지 않습니다."
                                      }
                                    }
                                    """)
                    )
            )
    })
    public ApiResponse<RecordsResponse> getRecords(
            @Parameter(
                    description = "Bearer {browserToken} 형식의 인증 헤더",
                    example = "Bearer b31390e4e3fe36fddbd05033b3068e01"
            )
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return ApiResponse.ok(recordsService.getRecords(extractBearerToken(authorization)));
    }

    private String extractBearerToken(String authorization) {
        if (!StringUtils.hasText(authorization) || !authorization.startsWith(BEARER_PREFIX)) {
            throw new CustomException(ErrorCode.INVALID_ROOM_PARTICIPANT);
        }

        String token = authorization.substring(BEARER_PREFIX.length());
        if (!StringUtils.hasText(token)) {
            throw new CustomException(ErrorCode.INVALID_ROOM_PARTICIPANT);
        }

        return token;
    }
}
