package com.vj.lets.web.global.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 오류 코드 및 오류 메세지 저장 객체
 *
 * @author VJ특공대 김종원
 * @version 1.0
 * @since 2023-09-21 (목)
 */
@Data
@AllArgsConstructor
public class ErrorResult {

    private String code;
    private String message;

}
