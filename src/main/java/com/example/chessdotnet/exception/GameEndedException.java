package com.example.chessdotnet.exception;

/**
 * 체스 게임이 이미 종료된 상태에서 이동을 시도할 때 발생하는 예외입니다.
 *
 * @author 전종영
 */
public class GameEndedException extends RuntimeException {
    /**
     * 지정된 메시지로 새 GameEndedException을 생성합니다.
     *
     * @param message 예외 메시지
     */
    public GameEndedException(String message) {
        super(message);
    }
}
