package com.sopt.sopkathon_web2_server.domain.home.dto.response;

public record HomeProgressResponse(
        Integer currentStep,
        Integer totalStep,
        String message
) {
}
