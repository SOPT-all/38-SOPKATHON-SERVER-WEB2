package com.sopt.sopkathon_web2_server.domain.records.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record RecordsResponse(
        @Schema(description = "기록 목록")
        List<RecordItemResponse> records
) {
}
