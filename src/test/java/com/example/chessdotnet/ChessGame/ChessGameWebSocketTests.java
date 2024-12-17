package com.example.chessdotnet.ChessGame;

import com.example.chessdotnet.dto.ChessMoveDTO;
import com.example.chessdotnet.service.WebSocketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 체스 게임 웹소켓 통신의 단위 테스트를 수행하는 클래스입니다.
 *
 * @author 전종영
 * @version 1.1
 * @since 2024-11-16
 */
@ExtendWith(MockitoExtension.class)
class ChessGameWebSocketTests {

    @Mock
    private WebSocketService webSocketService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private SimpMessageHeaderAccessor headerAccessor;

    private ChessGameTestController controller;

    private static final String TEST_SESSION_ID = "test-session";

    /**
     * 각 테스트 전에 필요한 Mock 객체들을 설정합니다.
     */
    @BeforeEach
    void setup() {
        when(headerAccessor.getSessionId()).thenReturn(TEST_SESSION_ID);
        controller = new ChessGameTestController(messagingTemplate, webSocketService);
    }

    /**
     * 체스 기물 이동 처리를 테스트합니다.
     */
    @Test
    void testHandleMove() {
        // given
        ChessMoveDTO moveDTO = createTestMoveDTO();

        // when
        controller.handleMove(moveDTO, headerAccessor);

        // then
        verify(messagingTemplate, times(1))
                .convertAndSend(eq("/topic/game/" + TEST_SESSION_ID), any(Map.class));
    }

    /**
     * 기권 처리를 테스트합니다.
     */
    @Test
    void testHandleResign() {
        // given
        Long userId = 1L;

        // when
        controller.handleResign(headerAccessor, userId);

        // then
        verify(messagingTemplate, times(1))
                .convertAndSend(eq("/topic/game/" + TEST_SESSION_ID), any(Map.class));
    }

    /**
     * 재접속 처리를 테스트합니다.
     */
    @Test
    void testHandleReconnect() {
        // given
        Map<String, Object> payload = createReconnectPayload();

        // when
        controller.handleReconnect(payload, headerAccessor);

        // then
        verify(messagingTemplate, times(1))
                .convertAndSendToUser(
                        eq(TEST_SESSION_ID),
                        eq("/queue/game.state"),
                        any(Map.class)
                );
    }

    /**
     * 테스트용 체스 이동 DTO를 생성하는 헬퍼 메서드입니다.
     *
     * @return 테스트용 ChessMoveDTO 객체
     */
    private ChessMoveDTO createTestMoveDTO() {
        ChessMoveDTO moveDTO = new ChessMoveDTO();

        ChessMoveDTO.PieceInfo pieceInfo = new ChessMoveDTO.PieceInfo();
        pieceInfo.setName("pawn");
        pieceInfo.setColor("white");
        moveDTO.setPiece(pieceInfo);

        ChessMoveDTO.Position startPos = new ChessMoveDTO.Position();
        startPos.setRow(6);
        startPos.setCol(0);
        moveDTO.setStartPosition(startPos);

        ChessMoveDTO.Position endPos = new ChessMoveDTO.Position();
        endPos.setRow(5);
        endPos.setCol(0);
        moveDTO.setEndPosition(endPos);

        return moveDTO;
    }

    /**
     * 테스트용 재접속 페이로드를 생성하는 헬퍼 메서드입니다.
     *
     * @return 재접속 정보를 담은 Map 객체
     */
    private Map<String, Object> createReconnectPayload() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", 1L);
        payload.put("sessionId", TEST_SESSION_ID);
        return payload;
    }
}