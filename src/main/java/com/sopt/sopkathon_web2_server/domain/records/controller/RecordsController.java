package com.sopt.sopkathon_web2_server.domain.records.controller;

import com.sopt.sopkathon_web2_server.domain.records.dto.response.RecordsResponse;
import com.sopt.sopkathon_web2_server.domain.records.service.RecordsService;
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
@RequestMapping("/api/records")
public class RecordsController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final RecordsService recordsService;

    @GetMapping
    public ApiResponse<RecordsResponse> getRecords(
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
