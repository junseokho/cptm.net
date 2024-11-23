package com.example.chessdotnet.controller;

import com.example.chessdotnet.dto.ChessMoveDTO;
import com.example.chessdotnet.entity.ChessGame;
import com.example.chessdotnet.service.ChessGameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;
import com.example.chessdotnet.service.ChessGameSession; /*<- 임시 */
import com.example.chessdotnet.service.GameStatus; /*<- 임시 */

/**
 * 체스 게임의 WebSocket 통신을 처리하는 컨트롤러입니다.
 * STOMP 프로토콜을 사용하여 실시간 게임 진행을 관리합니다.
 *
 * @author 전종영
 * @version 1.4
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

            /* 게임 상태 확인 */
            GameStatus gameStatus = chessGameService.checkGameClosed(sessionId);

            /* 현재 게임 세션 정보 조회 */
            ChessGameSession gameSession = chessGameService.getChessGameSession(sessionId);

            // 게임 상태 응답 생성
            Map<String, Object> response = new HashMap<>();
            response.put("moveSuccess", moveSuccess);
            if (moveSuccess) {
                response.put("move", moveDTO); // 이동 정보
            }
            response.put("status", gameStatus);
            response.put("whiteTurn", gameSession != null ? gameSession.isWhiteTurn() : false);

            // 이동 결과를 모든 참여자에게 브로드캐스트
            messagingTemplate.convertAndSend(
                    "/topic/game/" + sessionId,
                    response
            );

            // 게임이 종료된 경우 추가 메시지 전송
            // if (gameStatus != GameStatus.IN_PROGRESS) {
            //     messagingTemplate.convertAndSend(
            //             "/topic/game/" + sessionId + "/end",
            //             Map.of("status", gameStatus)
            //     );
            // }

        } catch (Exception e) {
            log.error("Error processing move: {}", e.getMessage());
            // 에러 메시지를 요청한 클라이언트에게만 전송
            messagingTemplate.convertAndSendToUser(
                    sessionId,
                    "/queue/errors",
                    Map.of("error", e.getMessage())
            );
        }
    }

    /**
     * 기권 요청을 처리합니다.
     * 누가 기권했는지에 따라 승자를 결정하고 결과를 전파합니다.
     *
     * @param headerAccessor WebSocket 세션 정보
     * @param userId 기권한 사용자의 ID
     */
    @MessageMapping("/chess.resign")
    public void handleResign(SimpMessageHeaderAccessor headerAccessor, Long userId) {
        String sessionId = headerAccessor.getSessionId();
        log.info("Resign request received - Session ID: {}", sessionId);

        try {
            boolean resignSuccess = chessGameService.doResign(sessionId);
            if (resignSuccess) {
                ChessGameSession gameSession = chessGameService.getChessGameSession(sessionId);

                // 기권 처리 결과를 모든 참여자에게 전파
                messagingTemplate.convertAndSend(
                        "/topic/game/" + sessionId,
                        Map.of(
                                "status", "RESIGNED",
                                "winner", gameSession.isWhitePlayer(userId) ? "BLACK" : "WHITE"
                        )
                );

            }
        } catch (Exception e) {
            log.error("Error processing resignation: {}", e.getMessage());
            messagingTemplate.convertAndSendToUser(
                    sessionId,
                    "/queue/errors",
                    Map.of("error", e.getMessage())
            );
        }
    }


}