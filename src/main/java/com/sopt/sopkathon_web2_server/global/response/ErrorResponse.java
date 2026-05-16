package com.sopt.sopkathon_web2_server.global.response;

import com.sopt.sopkathon_web2_server.global.exception.ErrorCode;
import jakarta.validation.constraints.NotNull;

public record ErrorResponse (
        @NotNull Integer code,
        @NotNull String message
) {

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage()
        );
    }
}
