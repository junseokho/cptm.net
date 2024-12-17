package com.example.chessdotnet.ChessGame;

import com.example.chessdotnet.dto.ChessMoveDTO;
import com.example.chessdotnet.entity.ChessGame;
import com.example.chessdotnet.service.WebSocketService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.util.Map;
import java.util.HashMap;

/**
 * 테스트 전용 체스 게임 웹소켓 컨트롤러입니다.
 * Room 의존성을 제거하고 웹소켓 통신 테스트에 필요한 기능만 포함합니다.
 *
 * @author 전종영
 * @version 1.0
 * @since 2024-11-16
 */
@Controller
public class ChessGameTestController {

    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketService webSocketService;

    public ChessGameTestController(SimpMessagingTemplate messagingTemplate,
                                   WebSocketService webSocketService) {
        this.messagingTemplate = messagingTemplate;
        this.webSocketService = webSocketService;
    }

    /**
     * 체스 기물 이동 요청을 처리합니다.
     *
     * @param moveDTO 이동 정보
     * @param headerAccessor 웹소켓 세션 정보
     */
    @MessageMapping("/chess.move")
    public void handleMove(@Payload ChessMoveDTO moveDTO, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();

        // 테스트를 위한 간단한 응답 생성
        Map<String, Object> response = new HashMap<>();
        response.put("moveSuccess", true);
        response.put("move", moveDTO);
        response.put("status", ChessGame.GameStatus.IN_PROGRESS);

        messagingTemplate.convertAndSend("/topic/game/" + sessionId, response);
    }

    /**
     * 기권 요청을 처리합니다.
     *
     * @param headerAccessor 웹소켓 세션 정보
     * @param userId 기권한 사용자의 ID
     */
    @MessageMapping("/chess.resign")
    public void handleResign(SimpMessageHeaderAccessor headerAccessor, Long userId) {
        String sessionId = headerAccessor.getSessionId();

        Map<String, Object> response = new HashMap<>();
        response.put("status", "RESIGNED");
        response.put("userId", userId);

        messagingTemplate.convertAndSend("/topic/game/" + sessionId, response);
    }

    /**
     * 재접속 요청을 처리합니다.
     *
     * @param payload 재접속 정보
     * @param headerAccessor 웹소켓 세션 정보
     */
    @MessageMapping("/chess.reconnect")
    public void handleReconnect(@Payload Map<String, Object> payload,
                                SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();

        Map<String, Object> response = new HashMap<>();
        response.put("reconnected", true);
        response.put("sessionId", sessionId);

        messagingTemplate.convertAndSendToUser(
                sessionId,
                "/queue/game.state",
                response
        );
    }
}