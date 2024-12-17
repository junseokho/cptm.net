package com.example.chessdotnet.exception;

/**
 * 잘못된 턴에 이동을 시도할 때 발생하는 예외입니다.
 *
 * @author 전종영
 */
public class InvalidTurnException extends RuntimeException {
    /**
     * 지정된 메시지로 새 InvalidTurnException을 생성합니다.
     *
     * @param message 예외 메시지
     */
    public InvalidTurnException(String message) {
        super(message);
    }
}
