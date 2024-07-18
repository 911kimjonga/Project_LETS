package com.vj.lets.web.global.exception;

import com.vj.lets.web.member.exception.MemberException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 컨트롤러에서 발생하는 예외 처리를 위해 공통적으로 사용할 구현 클래스
 *
 * @author VJ특공대 김종원
 * @version 1.0
 * @since 2023-09-21 (목)
 */
@RestControllerAdvice
@Slf4j
public class ControllerExceptionAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResult exception4xxHandle(IllegalArgumentException ex) {
        log.error("[Client 요청 오류] : {}", ex);
        return new ErrorResult("CLIENT-ERROR", ex.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult exception5xxHandle(Exception ex) {
        log.error("[Server 응답 오류] : {}", ex);
        return new ErrorResult("SERVER-ERROR", ex.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> userExcptionHandle(MemberException ex) {
        log.error("[User 응답 예외] : {}", ex);
        ErrorResult errorResult = new ErrorResult("USER-EXCEPTION", ex.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

}
