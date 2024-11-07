package com.example.chessdotnet.config;

import com.example.chessdotnet.dto.RoomDTO;
import com.example.chessdotnet.dto.RoomStatusMessage;
import com.example.chessdotnet.service.RoomWebSocketService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;

/**
 * WebSocket 메시징 서비스를 테스트하는 단위 테스트 클래스입니다.
 * SimpMessagingTemplate을 Mocking하여 메시지 전송을 검증합니다.
 *
 * @author 전종영
 */
@ExtendWith(MockitoExtension.class)
public class WebSocketServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private RoomWebSocketService webSocketService;

    @Captor
    private ArgumentCaptor<RoomStatusMessage> messageCaptor;

    /**
     * 방 상태 변경 알림이 올바르게 전송되는지 테스트합니다.
     */
    @Test
    void testNotifyRoomStatusChanged() {
        // Given
        RoomDTO roomDTO = new RoomDTO();
        roomDTO.setId(1L);
        roomDTO.setTitle("Test Room");
        roomDTO.setPlayersCount(2);

        // When
        webSocketService.notifyRoomStatusChanged(roomDTO, RoomStatusMessage.MessageType.PLAYER_JOINED);

        // Then
        verify(messagingTemplate).convertAndSend(
                eq("/topic/rooms/" + roomDTO.getId()),
                any(RoomStatusMessage.class)
        );

        verify(messagingTemplate).convertAndSend(
                any(String.class),
                messageCaptor.capture()
        );

        RoomStatusMessage capturedMessage = messageCaptor.getValue();
        assertEquals(RoomStatusMessage.MessageType.PLAYER_JOINED, capturedMessage.getType());
        assertEquals(roomDTO.getId(), capturedMessage.getRoomId());
        assertEquals(roomDTO, capturedMessage.getRoomDTO());
    }

    /**
     * 에러 메시지가 올바르게 전송되는지 테스트합니다.
     */
    @Test
    void testSendErrorMessage() {
        // Given
        Long roomId = 1L;
        String errorMessage = "Test error message";

        // When
        webSocketService.sendErrorMessage(roomId, errorMessage);

        // Then
        verify(messagingTemplate).convertAndSend(
                eq("/topic/rooms/" + roomId),
                any(RoomStatusMessage.class)
        );

        verify(messagingTemplate).convertAndSend(
                any(String.class),
                messageCaptor.capture()
        );

        RoomStatusMessage capturedMessage = messageCaptor.getValue();
        assertEquals(RoomStatusMessage.MessageType.ERROR, capturedMessage.getType());
        assertEquals(roomId, capturedMessage.getRoomId());
        assertEquals(errorMessage, capturedMessage.getMessage());
    }
}