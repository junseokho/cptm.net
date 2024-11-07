package com.example.chessdotnet.exception;

/**
 * 체스 게임 상태가 유효하지 않을 때 발생하는 예외입니다.
 *
 * @author 전종영
 */
public class InvalidGameStateException extends RuntimeException {
    /**
     * 지정된 메시지로 새 InvalidGameStateException을 생성합니다.
     *
     * @param message 예외 메시지
     */
    public InvalidGameStateException(String message) {
        super(message);
    }
}
