package com.sopt.sopkathon_web2_server.domain.upload.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Presigned URL 발급 응답")
public record PresignedUrlResponse(
        @Schema(description = "S3에 직접 업로드할 Presigned URL", example = "https://bucket.s3.ap-northeast-2.amazonaws.com/uploads/answer.mp4?X-Amz-Algorithm=AWS4-HMAC-SHA256")
        String uploadUrl,
        @Schema(description = "업로드 후 서버에 저장할 파일 키", example = "uploads/answer.mp4")
        String key
) {
}
