package com.sopt.sopkathon_web2_server.domain.records.dto.response;

import java.util.List;

public record RecordsResponse(
        List<RecordItemResponse> records
) {
}
