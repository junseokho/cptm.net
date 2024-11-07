package com.example.chessdotnet.exception;

/**
 * 잘못된 체스 기물 이동 시도 시 발생하는 예외입니다.
 *
 * @author 전종영
 */
public class IllegalMoveException extends RuntimeException {
  /**
   * 지정된 메시지로 새 IllegalMoveException을 생성합니다.
   *
   * @param message 예외 메시지
   */
  public IllegalMoveException(String message) {
    super(message);
  }
}
