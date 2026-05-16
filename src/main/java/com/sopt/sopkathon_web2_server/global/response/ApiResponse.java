package com.sopt.sopkathon_web2_server.global.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sopt.sopkathon_web2_server.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.jspecify.annotations.Nullable;

public record ApiResponse<T>(
        @JsonIgnore
        HttpStatus httpStatus,
        boolean success,
        @Nullable T data,
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
