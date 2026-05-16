package com.sopt.sopkathon_web2_server.domain.upload.controller;

import com.sopt.sopkathon_web2_server.domain.upload.dto.request.PresignedUrlRequest;
import com.sopt.sopkathon_web2_server.domain.upload.dto.response.PresignedUrlResponse;
import com.sopt.sopkathon_web2_server.domain.upload.service.UploadService;
import com.sopt.sopkathon_web2_server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/uploads")
@Tag(name = "Uploads", description = "파일 업로드 준비 API")
public class UploadController {

    private final UploadService uploadService;

    @Operation(summary = "Presigned URL 발급", description = "영상 업로드에 사용할 S3 Presigned URL과 저장 키를 발급합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Presigned URL 발급 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 브라우저 토큰 또는 업로드 요청")
    })
    @PostMapping("/presigned-url")
    public ApiResponse<PresignedUrlResponse> createPresignedUrl(
            @Parameter(description = "Bearer 브라우저 토큰", example = "Bearer valid-browser-token", required = true)
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader,
            @RequestBody PresignedUrlRequest request
    ) {
        return ApiResponse.ok(uploadService.createPresignedUrl(request, authorizationHeader));
    }
}
