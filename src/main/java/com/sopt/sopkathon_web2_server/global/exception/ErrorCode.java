package com.sopt.sopkathon_web2_server.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    TEST_ERROR(10000, HttpStatus.BAD_REQUEST, "테스트 에러입니다."),
    INVALID_UPLOAD_REQUEST(40001, HttpStatus.BAD_REQUEST, "파일 업로드 요청이 올바르지 않습니다."),
    NOT_FOUND_END_POINT(40400, HttpStatus.NOT_FOUND, "존재하지 않는 API입니다."),
    INTERNAL_SERVER_ERROR(50000, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.");

    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;
}
