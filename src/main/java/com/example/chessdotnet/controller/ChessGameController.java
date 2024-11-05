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

import java.util.Map;
import java.util.HashMap;

/**
 * 체스 게임 관련 WebSocket 메시지를 처리하는 컨트롤러입니다.
 *
 * @author 전종영
 * @version 1.0
 * @since 2024-11-05
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ChessGameController {
    private final ChessGameService chessGameService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 체스 기물 이동 메시지를 처리합니다.
     *
     * @param moveDTO 이동 정보
     * @param headerAccessor WebSocket 세션 정보
     */
    @MessageMapping("/chess.move")
    public void handleMove(@Payload ChessMoveDTO moveDTO, SimpMessageHeaderAccessor headerAccessor) {
        try {
            Long roomId = (Long) headerAccessor.getSessionAttributes().get("roomId");
            Long playerId = (Long) headerAccessor.getSessionAttributes().get("userId");

            ChessGame updatedGame = chessGameService.doMove(roomId, moveDTO, playerId);

            // 게임 상태 변경을 모든 참여자에게 브로드캐스트
            messagingTemplate.convertAndSend(
                    "/topic/chess.game." + roomId,
                    updatedGame
            );

            // 게임이 종료된 경우 추가 메시지 전송
            if (updatedGame.getStatus() != ChessGame.GameStatus.IN_PROGRESS) {
                messagingTemplate.convertAndSend(
                        "/topic/chess.game." + roomId + ".end",
                        Map.of("status", updatedGame.getStatus())
                );
            }
        } catch (Exception e) {
            log.error("Error handling chess move", e);
            messagingTemplate.convertAndSendToUser(
                    headerAccessor.getUser().getName(),
                    "/queue/errors",
                    e.getMessage()
            );
        }
    }
}