package com.sopt.sopkathon_web2_server.domain.records.dto.response;

import java.time.LocalDateTime;

public record RecordItemResponse(
        Long roomQuestionId,
        String question,
        LocalDateTime completedAt
) {
}
