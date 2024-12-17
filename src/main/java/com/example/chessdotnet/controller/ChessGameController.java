package com.example.chessdotnet.controller;

import com.example.chessdotnet.dto.ChessMoveDTO;
import com.example.chessdotnet.entity.ChessGame;
import com.example.chessdotnet.service.chessGameSession.ChessGameService;
import com.example.chessdotnet.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import com.example.chessdotnet.service.chessGameSession.*;

/**
 * 체스 게임의 WebSocket 통신을 처리하는 컨트롤러입니다.
 * STOMP 프로토콜을 사용하여 실시간 게임 진행을 관리합니다.
 *
 * @author 전종영
 * @version 2.0
 * @since 2024-11-16
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ChessGameController {
    private final ChessGameService chessGameService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 체스 기물 이동 요청을 처리합니다.
     * 클라이언트로부터 WebSocket을 통해 전송된 이동 요청을 처리하고 결과를 전파합니다.
     *
     * @param moveDTO 이동 정보
     * @param headerAccessor WebSocket 세션 정보
     */
    @MessageMapping("/chess.move")
    public void handleMove(@Payload ChessMoveDTO moveDTO, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        log.info("Move request received - Session ID: {}, Move: {}", sessionId, moveDTO);

        try {
            // 이동 실행
            boolean moveSuccess = chessGameService.doMove(sessionId, moveDTO);

            // 게임 상태 체크
            ChessGame.GameStatus gameStatus = chessGameService.checkGameStatus(sessionId);

            // 게임 세션 정보 조회
            ChessGameSession gameSession = chessGameService.getChessGameSession(sessionId);

            // 응답 생성
            Map<String, Object> response = createMoveResponse(moveSuccess, moveDTO, gameStatus, gameSession);

            // 결과 브로드캐스트
            broadcastGameState(sessionId, response);

            // 게임 종료 처리
            if (isGameEnded(gameStatus)) {
                handleGameEnd(sessionId, gameStatus);
            }

        } catch (IllegalMoveException | InvalidTurnException e) {
            handleMoveError(sessionId, e);
        } catch (Exception e) {
            handleUnexpectedError(sessionId, e);
        }
    }

    /**
     * 기권 요청을 처리합니다.
     *
     * @param headerAccessor WebSocket 세션 정보
     * @param userId 기권한 사용자의 ID
     */
    @MessageMapping("/chess.resign")
    public void handleResign(SimpMessageHeaderAccessor headerAccessor, Long userId) {
        String sessionId = headerAccessor.getSessionId();
        log.info("Resign request received - Session ID: {}, User ID: {}", sessionId, userId);

        try {
            ChessGameSession gameSession = chessGameService.getChessGameSession(sessionId);
            boolean resignSuccess = chessGameService.doResign(sessionId, userId);

            if (resignSuccess) {
                Map<String, Object> response = createResignResponse(gameSession, userId);
                broadcastGameState(sessionId, response);
            }
        } catch (Exception e) {
            handleResignError(sessionId, e);
        }
    }

    @MessageMapping("/chess.reconnect")
    public void handleReconnect(@Payload Map<String, Object> payload,
                                SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        Long userId = Long.valueOf(payload.get("userId").toString());
        Long gameId = Long.valueOf(payload.get("gameId").toString());

        boolean reconnected = chessGameService.handleReconnect(userId, sessionId, gameId);
        if (!reconnected) {
            messagingTemplate.convertAndSendToUser(
                    sessionId,
                    "/queue/errors",
                    Map.of("error", "Reconnection failed")
            );
        }
    }

    @MessageExceptionHandler
    public void handleException(Exception ex, Principal principal) {
        log.error("Error for user {}: {}", principal.getName(), ex.getMessage());
        messagingTemplate.convertAndSendToUser(principal.getName(),
                "/queue/errors", Map.of("error", ex.getMessage()));
    }

    /**
     * 이동 응답을 생성합니다.
     */
    private Map<String, Object> createMoveResponse(boolean moveSuccess, ChessMoveDTO moveDTO,
                                                   ChessGame.GameStatus gameStatus, ChessGameSession gameSession) {
        Map<String, Object> response = new HashMap<>();
        response.put("moveSuccess", moveSuccess);
        if (moveSuccess) {
            response.put("move", moveDTO);
        }
        response.put("status", gameStatus);
        response.put("whiteTurn", gameSession != null ? gameSession.isWhiteTurn() : false);
        return response;
    }

    /**
     * 기권 응답을 생성합니다.
     */
    private Map<String, Object> createResignResponse(ChessGameSession gameSession, Long userId) {
        return Map.of(
                "status", "RESIGNED",
                "winner", gameSession.isWhitePlayer(userId) ? "BLACK" : "WHITE"
        );
    }

    /**
     * 게임 상태를 모든 참여자에게 브로드캐스트합니다.
     */
    private void broadcastGameState(String sessionId, Map<String, Object> response) {
        messagingTemplate.convertAndSend("/topic/game/" + sessionId, response);
    }

    /**
     * 게임 종료를 처리합니다.
     */
    private void handleGameEnd(String sessionId, ChessGame.GameStatus gameStatus) {
        messagingTemplate.convertAndSend(
                "/topic/game/" + sessionId + "/end",
                Map.of("status", gameStatus)
        );
    }

    /**
     * 이동 관련 에러를 처리합니다.
     *
     * @param sessionId 세션 ID
     * @param e 발생한 예외
     */
    private void handleMoveError(String sessionId, Exception e) {
        log.error("Move error: {}", e.getMessage());
        messagingTemplate.convertAndSendToUser(
                sessionId,
                "/queue/errors",
                Map.of("error", e.getMessage())
        );
    }

    /**
     * 예기치 않은 에러를 처리합니다.
     */
    private void handleUnexpectedError(String sessionId, Exception e) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        messagingTemplate.convertAndSendToUser(
                sessionId,
                "/queue/errors",
                Map.of("error", "An unexpected error occurred")
        );
    }

    /**
     * 기권 관련 에러를 처리합니다.
     */
    private void handleResignError(String sessionId, Exception e) {
        log.error("Resign error: {}", e.getMessage());
        messagingTemplate.convertAndSendToUser(
                sessionId,
                "/queue/errors",
                Map.of("error", e.getMessage())
        );
    }

    /**
     * 게임이 종료되었는지 확인합니다.
     */
    private boolean isGameEnded(ChessGame.GameStatus status) {
        return status == ChessGame.GameStatus.CHECKMATE ||
                status == ChessGame.GameStatus.STALEMATE ||
                status == ChessGame.GameStatus.DRAW;
    }
}