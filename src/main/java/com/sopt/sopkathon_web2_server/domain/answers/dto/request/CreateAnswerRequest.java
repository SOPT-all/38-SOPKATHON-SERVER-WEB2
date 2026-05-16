package com.sopt.sopkathon_web2_server.domain.answers.dto.request;

public record CreateAnswerRequest(
        Long roomQuestionId,
        String imageKey
) {
}
