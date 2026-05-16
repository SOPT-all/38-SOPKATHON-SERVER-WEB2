package com.sopt.sopkathon_web2_server.domain.upload.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Presigned URL 발급 요청")
public record PresignedUrlRequest(
        @Schema(description = "업로드할 파일 이름", example = "answer.mp4")
        String fileName,
        @Schema(description = "업로드할 파일 MIME 타입", example = "video/mp4")
        String contentType
) {
}
