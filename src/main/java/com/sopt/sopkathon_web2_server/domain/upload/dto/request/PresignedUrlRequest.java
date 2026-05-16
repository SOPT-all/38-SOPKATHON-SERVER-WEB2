package com.sopt.sopkathon_web2_server.domain.upload.dto.request;

public record PresignedUrlRequest(
        String fileName,
        String contentType
) {
}
