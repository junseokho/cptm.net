package com.example.chessdotnet.dto.game;

/**
 * 게임 상태 변경의 유형을 정의하는 열거형입니다.
 *
 * @author 전종영
 * @version 1.0
 * @since 2024-11-16
 */
public enum GameStateChangeType {
    PLAYER_DISCONNECTED,   // 플레이어 연결 끊김
    PLAYER_RECONNECTED,    // 플레이어 재접속
    GAME_ENDED,           // 게임 종료
    TIME_UPDATED,         // 시간 업데이트
    MOVE_MADE            // 기물 이동
}