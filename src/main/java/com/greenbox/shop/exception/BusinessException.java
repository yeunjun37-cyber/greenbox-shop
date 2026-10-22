package com.greenbox.shop.exception;

/** 비즈니스 규칙 위반 시 사용자에게 메시지를 보여주기 위한 예외 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
