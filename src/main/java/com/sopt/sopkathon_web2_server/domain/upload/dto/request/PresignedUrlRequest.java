package com.sopt.sopkathon_web2_server.domain.upload.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record PresignedUrlRequest(
        @Schema(description = "업로드할 파일명", example = "answer-image.png")
        String fileName,
        @Schema(description = "파일 Content-Type", example = "image/png")
        String contentType
) {
}
