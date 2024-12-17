package com.example.chessdotnet.dto.game;

import java.util.Collections;

/**
 * 게임 종료 이벤트를 나타내는 클래스입니다.
 *
 * @author 전종영
 * @version 1.0
 * @since 2024-11-16
 */
public class GameEndEvent extends GameStateChangeEvent {
    public GameEndEvent(Long gameId, GameEndReason reason) {
        super(GameStateChangeType.GAME_ENDED,
                gameId,
                Collections.singletonMap("reason", reason));
    }
}