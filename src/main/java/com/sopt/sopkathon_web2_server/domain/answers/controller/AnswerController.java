package com.sopt.sopkathon_web2_server.domain.answers.controller;

import com.sopt.sopkathon_web2_server.domain.answers.dto.request.CreateAnswerRequest;
import com.sopt.sopkathon_web2_server.domain.answers.dto.response.CreateAnswerResponse;
import com.sopt.sopkathon_web2_server.domain.answers.service.AnswerService;
import com.sopt.sopkathon_web2_server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/answers")
@Tag(name = "Answers", description = "답변 등록 API")
public class AnswerController {

    private final AnswerService answerService;

    @Operation(summary = "답변 등록", description = "브라우저 토큰으로 참여자를 확인하고 오늘 질문에 대한 영상 답변을 등록합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "답변 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 브라우저 토큰 또는 질문"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 등록된 답변")
    })
    @PostMapping
    public ApiResponse<CreateAnswerResponse> createAnswer(
            @Parameter(description = "Bearer 브라우저 토큰", example = "Bearer valid-browser-token", required = true)
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader,
            @RequestBody CreateAnswerRequest request
    ) {
        return ApiResponse.created(answerService.createAnswer(request, authorizationHeader));
    }
}
