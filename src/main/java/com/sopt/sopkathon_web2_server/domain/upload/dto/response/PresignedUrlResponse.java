package com.sopt.sopkathon_web2_server.domain.upload.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record PresignedUrlResponse(
        @Schema(description = "S3 업로드용 presigned URL", example = "https://bucket.s3.ap-northeast-2.amazonaws.com/uploads/example.png?...signature=...")
        String uploadUrl,
        @Schema(description = "업로드된 객체 key", example = "uploads/example.png")
        String key
) {
}
