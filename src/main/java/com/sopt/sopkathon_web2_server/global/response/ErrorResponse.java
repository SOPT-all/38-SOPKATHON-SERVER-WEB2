package com.sopt.sopkathon_web2_server.global.response;

import com.sopt.sopkathon_web2_server.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ErrorResponse (
        @Schema(description = "에러 코드", example = "40002")
        @NotNull Integer code,
        @Schema(description = "에러 메시지", example = "초대 토큰이 올바르지 않습니다.")
        @NotNull String message
) {

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage()
        );
    }
}
