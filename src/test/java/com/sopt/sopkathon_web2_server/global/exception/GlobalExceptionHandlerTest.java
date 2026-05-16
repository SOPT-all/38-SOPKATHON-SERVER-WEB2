// 전역 예외 핸들러의 응답과 로그 출력을 검증하는 테스트
package com.sopt.sopkathon_web2_server.global.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(OutputCaptureExtension.class)
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleCustomExceptionLogsStackTrace(CapturedOutput output) {
        exceptionHandler.handleCustomException(new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));

        assertThat(output)
                .contains("handleCustomException() in GlobalExceptionHandler throw CustomException")
                .contains("com.sopt.sopkathon_web2_server.global.exception.CustomException");
    }
}
