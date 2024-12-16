package com.example.chessdotnet.dto.game;

import java.util.Map;

/**
 * 게임 상태 변경 이벤트를 나타내는 클래스입니다.
 *
 * @author 전종영
 */
public class GameStateEvent extends GameStateChangeEvent {
    /**
     * 게임 상태 변경 이벤트를 생성합니다.
     *
     * @param type 이벤트 타입
     * @param gameId 게임 ID
     * @param payload 이벤트 데이터
     */
    public GameStateEvent(GameStateChangeType type, Long gameId, Map<String, Object> payload) {
        super(type, gameId, payload);
    }
}
