package com.example.chessdotnet.service;

import com.example.chessdotnet.entity.ChessGame;
import com.example.chessdotnet.service.chessGameSession.ChessGameSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * WebSocket 서비스 클래스입니다.
 *
 * @author 전종영
 */
@Service
@Slf4j
public class WebSocketService {
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 게임 상태를 특정 세션에 알립니다.
     *
     * @param sessionId 대상 세션 ID
     * @param gameSession 게임 세션 정보
     */
    public void notifyGameState(String sessionId, ChessGameSession gameSession) {
        messagingTemplate.convertAndSendToUser(
                sessionId,
                "/queue/game.state",
                Map.of(
                        "board", gameSession.getChessboard(),
                        "leftTime", gameSession.getLeftTime(),
                        "currentTurn", gameSession.isWhiteTurn()
                )
        );
    }

    /**
     * 게임 오류를 특정 세션에 알립니다.
     *
     * @param sessionId 대상 세션 ID
     * @param error 오류 메시지
     */
    public void notifyError(String sessionId, String error) {
        messagingTemplate.convertAndSendToUser(
                sessionId,
                "/queue/errors",
                Map.of("error", error)
        );
        log.error("Error notification sent to session {}: {}", sessionId, error);
    }

    /**
     * 게임 종료를 모든 참가자에게 알립니다.
     *
     * @param gameId 게임 ID
     * @param status 게임 종료 상태
     */
    public void notifyGameEnd(String gameId, ChessGame.GameStatus status) {
        messagingTemplate.convertAndSend(
                "/topic/game/" + gameId + "/end",
                Map.of("status", status)
        );
        log.info("Game end notification sent for game {}: {}", gameId, status);
    }
}
