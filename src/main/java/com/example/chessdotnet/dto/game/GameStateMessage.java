package com.example.chessdotnet.dto.game;

import lombok.Getter;
import lombok.AllArgsConstructor;
import java.util.Map;

/**
 * 게임 상태 변경을 클라이언트에게 전달하기 위한 메시지 DTO입니다.
 *
 * @author 전종영
 * @version 1.0
 * @since 2024-11-16
 */
@Getter
@AllArgsConstructor
public class GameStateMessage {
    private GameStateChangeType type;
    private Map<String, Object> payload;
}