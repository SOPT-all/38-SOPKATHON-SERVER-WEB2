package com.sopt.sopkathon_web2_server.domain.upload.dto.response;

public record PresignedUrlResponse(
        String uploadUrl,
        String key
) {
}
