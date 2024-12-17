package com.example.chessdotnet.dto.game;

import lombok.Getter;
import lombok.AllArgsConstructor;
import java.util.Map;

/**
 * 게임 상태 변경 이벤트를 나타내는 클래스입니다.
 *
 * @author 전종영
 * @version 1.0
 * @since 2024-11-16
 */
@Getter
@AllArgsConstructor
public class GameStateChangeEvent {
    private GameStateChangeType type;
    private Long gameId;
    private Map<String, Object> payload;
}