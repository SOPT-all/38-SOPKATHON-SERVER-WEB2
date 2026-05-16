package com.sopt.sopkathon_web2_server.domain.home.dto.response;

public record TodayQuestionResponse(
        Long roomQuestionId,
        String content,
        Boolean answered
) {
}
