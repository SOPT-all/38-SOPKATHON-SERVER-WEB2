package com.sopt.sopkathon_web2_server.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    TEST_ERROR(10000, HttpStatus.BAD_REQUEST, "테스트 에러입니다."),
    INVALID_UPLOAD_REQUEST(40001, HttpStatus.BAD_REQUEST, "파일 업로드 요청이 올바르지 않습니다."),
    INVALID_INVITE_TOKEN(40002, HttpStatus.BAD_REQUEST, "초대 토큰이 올바르지 않습니다."),
    INVALID_BROWSER_TOKEN(40003, HttpStatus.BAD_REQUEST, "브라우저 토큰이 올바르지 않습니다."),
    ROOM_ALREADY_FULL(40901, HttpStatus.CONFLICT, "이미 참여자가 모두 입장한 방입니다."),
    INVALID_ROOM_PARTICIPANT(40004, HttpStatus.BAD_REQUEST, "방 참여자 정보가 올바르지 않습니다."),
    INVALID_ROLE_SWAP_REQUEST(40005, HttpStatus.BAD_REQUEST, "역할을 변경할 수 없습니다."),
    QUESTION_NOT_FOUND(40401, HttpStatus.NOT_FOUND, "질문을 찾을 수 없습니다."),
    RECORD_NOT_FOUND(40402, HttpStatus.NOT_FOUND, "기록을 찾을 수 없습니다"),
    ANSWER_ALREADY_EXISTS(40902, HttpStatus.CONFLICT, "이미 답변한 질문입니다."),
    NOT_FOUND_END_POINT(40400, HttpStatus.NOT_FOUND, "존재하지 않는 API입니다."),
    INTERNAL_SERVER_ERROR(50000, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.");

    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;
}
