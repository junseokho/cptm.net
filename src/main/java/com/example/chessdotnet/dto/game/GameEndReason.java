package com.example.chessdotnet.dto.game;

/**
 * 게임 종료 사유를 정의하는 열거형입니다.
 *
 * @author 전종영
 * @version 1.0
 * @since 2024-11-16
 */
public enum GameEndReason {
    CHECKMATE,            // 체크메이트
    STALEMATE,           // 스테일메이트
    RESIGNATION,         // 기권
    TIME_OUT,           // 시간 초과
    DISCONNECT_TIMEOUT  // 연결 끊김 시간 초과
}