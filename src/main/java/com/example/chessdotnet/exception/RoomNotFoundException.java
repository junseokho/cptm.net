package com.example.chessdotnet.exception;

public class RoomNotFoundException extends RuntimeException {
    public RoomNotFoundException(String message) {
        super(message);
    }
}
// 방을 찾을 수 없을 때 발생하는 사용자 정의 예외