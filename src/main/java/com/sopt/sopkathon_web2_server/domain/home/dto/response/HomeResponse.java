package com.sopt.sopkathon_web2_server.domain.home.dto.response;

public record HomeResponse(
        String selectedMode,
        String statusMessage,
        HomeProgressResponse progress,
        TodayQuestionResponse todayQuestion
) {
}
