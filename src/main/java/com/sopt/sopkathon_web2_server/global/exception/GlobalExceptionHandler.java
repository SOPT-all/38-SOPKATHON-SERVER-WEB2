package com.sopt.sopkathon_web2_server.global.exception;

import com.sopt.sopkathon_web2_server.global.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = {NoHandlerFoundException.class, HttpRequestMethodNotSupportedException.class})
    public ApiResponse<?> handleNoPageFoundException(Exception e) {
        log.error("GlobalExceptionHandler catch NoHandlerFoundException : {}", e.getMessage());
        return ApiResponse.fail(ErrorCode.NOT_FOUND_END_POINT);
    }

    @ExceptionHandler(value = {CustomException.class})
    public ApiResponse<?> handleCustomException(CustomException e) {
        log.error("handleCustomException() in GlobalExceptionHandler throw CustomException : {}", e.getMessage());
        return ApiResponse.fail(e.getErrorCode());
    }

    @ExceptionHandler(value = {Exception.class})
    public ApiResponse<?> handleException(Exception e) {
        log.error("handleException() in GlobalExceptionHandler throw Exception", e);
        return ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
