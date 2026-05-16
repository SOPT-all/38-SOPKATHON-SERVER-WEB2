package com.sopt.sopkathon_web2_server.global.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sopt.sopkathon_web2_server.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.jspecify.annotations.Nullable;

public record ApiResponse<T>(
        @JsonIgnore
        HttpStatus httpStatus,
        @Schema(description = "요청 성공 여부", example = "true")
        boolean success,
        @Schema(description = "응답 데이터")
        @Nullable T data,
        @Schema(description = "에러 정보")
        @Nullable ErrorResponse error
) {

    public static <T> ApiResponse<T> ok(@Nullable final T data) {
        return new ApiResponse<>(HttpStatus.OK, true, data, null);
    }

    public static <T> ApiResponse<T> created(@Nullable final T data) {
        return new ApiResponse<>(HttpStatus.CREATED, true, data, null);
    }

    public static <T> ApiResponse<T> fail(final ErrorCode errorCode) {
        return new ApiResponse<>(errorCode.getHttpStatus(), false, null, ErrorResponse.of(errorCode)
        );
    }
}
