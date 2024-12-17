package com.example.chessdotnet.exception;

/**
 * 요청한 방을 찾을 수 없을 때 발생하는 예외입니다.
 *
 * @author 전종영
 */
public class RoomNotFoundException extends RuntimeException {
    /**
     * 지정된 오류 메시지로 새 RoomNotFoundException을 생성합니다.
     *
     * @param message 예외에 대한 상세 메시지
     */
    public RoomNotFoundException(String message) {
        super(message);
    }
}