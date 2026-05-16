package com.sopt.sopkathon_web2_server.domain.answers.controller;

import com.sopt.sopkathon_web2_server.domain.answers.dto.request.CreateAnswerRequest;
import com.sopt.sopkathon_web2_server.domain.answers.dto.response.CreateAnswerResponse;
import com.sopt.sopkathon_web2_server.domain.answers.service.AnswerService;
import com.sopt.sopkathon_web2_server.global.response.ApiResponse;
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
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping
    public ApiResponse<CreateAnswerResponse> createAnswer(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader,
            @RequestBody CreateAnswerRequest request
    ) {
        return ApiResponse.created(answerService.createAnswer(request, authorizationHeader));
    }
}
