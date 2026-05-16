package com.sopt.sopkathon_web2_server.domain.answers.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record CreateAnswerResponse(
        Long answerId,
        Long roomQuestionId,
        String videoKey,
        @JsonProperty("isUnlocked")
        Boolean isUnlocked,
        Integer currentStreakDay,
        String currentStreakMessage,
        LocalDateTime createdAt
) {
}
