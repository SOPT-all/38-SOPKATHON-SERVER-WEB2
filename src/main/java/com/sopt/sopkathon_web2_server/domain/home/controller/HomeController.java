package com.sopt.sopkathon_web2_server.domain.home.controller;

import com.sopt.sopkathon_web2_server.domain.home.dto.response.HomeResponse;
import com.sopt.sopkathon_web2_server.domain.home.service.HomeService;
import com.sopt.sopkathon_web2_server.global.exception.CustomException;
import com.sopt.sopkathon_web2_server.global.exception.ErrorCode;
import com.sopt.sopkathon_web2_server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/home")
@Tag(name = "Home", description = "홈 화면 조회 API")
public class HomeController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final HomeService homeService;

    @Operation(summary = "홈 화면 조회", description = "브라우저 토큰으로 참여자를 확인하고 현재 진행 상태와 오늘 질문을 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "홈 화면 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 방 참여자")
    })
    @GetMapping
    public ApiResponse<HomeResponse> getHome(
            @Parameter(description = "Bearer 브라우저 토큰", example = "Bearer valid-browser-token", required = true)
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        return ApiResponse.ok(homeService.getHome(extractBearerToken(authorization)));
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
