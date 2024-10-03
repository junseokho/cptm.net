package com.example.chessdotnet.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
// 사용자를 찾을 수 없을 때 발생하는 사용자 정의 예외