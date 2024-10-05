package com.example.chessdotnet.exception;

/**
 * 요청한 사용자를 찾을 수 없을 때 발생하는 예외입니다.
 */
public class UserNotFoundException extends RuntimeException {
    /**
     * 지정된 오류 메시지로 새 UserNotFoundException을 생성합니다.
     *
     * @param message 예외에 대한 상세 메시지
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}