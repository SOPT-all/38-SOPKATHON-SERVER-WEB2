package com.sopt.sopkathon_web2_server.domain.home.controller;

import com.sopt.sopkathon_web2_server.domain.home.dto.response.HomeResponse;
import com.sopt.sopkathon_web2_server.domain.home.service.HomeService;
import com.sopt.sopkathon_web2_server.global.exception.CustomException;
import com.sopt.sopkathon_web2_server.global.exception.ErrorCode;
import com.sopt.sopkathon_web2_server.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/home")
public class HomeController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final HomeService homeService;

    @GetMapping
    public ApiResponse<HomeResponse> getHome(
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
