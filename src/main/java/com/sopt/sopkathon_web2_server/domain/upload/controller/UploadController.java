package com.sopt.sopkathon_web2_server.domain.upload.controller;

import com.sopt.sopkathon_web2_server.domain.upload.dto.request.PresignedUrlRequest;
import com.sopt.sopkathon_web2_server.domain.upload.dto.response.PresignedUrlResponse;
import com.sopt.sopkathon_web2_server.domain.upload.service.UploadService;
import com.sopt.sopkathon_web2_server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Tag(name = "Upload", description = "업로드 URL 발급 API")
public class UploadController {

    private final UploadService uploadService;

    @PostMapping("/presigned-url")
    @Operation(summary = "Presigned URL 발급", description = "브라우저 토큰을 검증한 뒤 S3 업로드용 presigned URL을 발급합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Presigned URL 발급 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "data": {
                                        "uploadUrl": "https://bucket.s3.ap-northeast-2.amazonaws.com/uploads/example.png?...",
                                        "key": "uploads/example.png"
                                      },
                                      "error": null
                                    }
                                    """)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 업로드 요청 또는 브라우저 토큰",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "INVALID_UPLOAD_REQUEST",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "data": null,
                                                      "error": {
                                                        "code": 40001,
                                                        "message": "파일 업로드 요청이 올바르지 않습니다."
                                                      }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "INVALID_BROWSER_TOKEN",
                                            value = """
                                                    {
                                                      "success": false,
                                                      "data": null,
                                                      "error": {
                                                        "code": 40003,
                                                        "message": "브라우저 토큰이 올바르지 않습니다."
                                                      }
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    public ApiResponse<PresignedUrlResponse> createPresignedUrl(
            @Parameter(
                    description = "Bearer {browserToken} 형식의 인증 헤더",
                    example = "Bearer b31390e4e3fe36fddbd05033b3068e01"
            )
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader,
            @RequestBody PresignedUrlRequest request
    ) {
        return ApiResponse.ok(uploadService.createPresignedUrl(request, authorizationHeader));
    }
}
