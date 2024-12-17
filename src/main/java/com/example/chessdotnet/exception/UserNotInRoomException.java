package com.example.chessdotnet.exception;

/**
 * 사용자가 방에 존재하지 않을 때 발생하는 예외입니다.
 *
 * @author 전종영
 */
public class UserNotInRoomException extends RuntimeException {
    /**
     * 지정된 오류 메시지로 새 UserNotInRoomException을 생성합니다.
     *
     * @param message 예외에 대한 상세 메시지
     */
    public UserNotInRoomException(String message) {
        super(message);
    }
}