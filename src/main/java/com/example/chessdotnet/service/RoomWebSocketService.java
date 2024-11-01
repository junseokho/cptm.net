package com.example.chessdotnet.service;

import com.example.chessdotnet.dto.RoomDTO;
import com.example.chessdotnet.dto.RoomStatusMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * 방 상태 변경을 처리하고 웹소켓을 통해 클라이언트에게 알림을 전송하는 서비스 클래스입니다.
 * 이 클래스는 실시간으로 방의 상태 변경사항을 모든 참여자에게 전파하는 역할을 합니다.
 *
 * @author 전종영
 * @version 1.0
 * @since 2024-11-01
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoomWebSocketService {
    /** 웹소켓 메시지를 전송하기 위한 Spring 메시징 템플릿입니다. */
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 방의 상태가 변경되었을 때 해당 방의 모든 참여자에게 알림을 전송합니다.
     * 메시지 전송 실패 시 로그를 기록하고 계속 진행합니다.
     *
     * @param room 상태가 변경된 방의 정보를 담고 있는 DTO
     * @param type 발생한 상태 변경의 유형
     * @see RoomDTO
     * @see RoomStatusMessage.MessageType
     */
    public void notifyRoomStatusChanged(RoomDTO room, RoomStatusMessage.MessageType type) {
        try {
            RoomStatusMessage message = new RoomStatusMessage();
            message.setType(type);
            message.setRoomId(room.getId());
            message.setRoomDTO(room);

            String destination = "/topic/rooms/" + room.getId();
            messagingTemplate.convertAndSend(destination, message);

            log.info("Room status notification sent - Room ID: {}, Type: {}, Players: {}",
                    room.getId(), type, room.getPlayersCount());
        } catch (Exception e) {
            // 메시지 전송 실패 시 로그만 남기고 계속 진행
            log.error("Failed to send room status notification - Room ID: {}, Type: {}, Error: {}",
                    room.getId(), type, e.getMessage());
        }
    }

    /**
     * 특정 방의 참여자들에게 에러 메시지를 전송합니다.
     * 메시지 전송 실패 시 로그를 기록하고 계속 진행합니다.
     *
     * @param roomId 에러가 발생한 방의 ID
     * @param errorMessage 에러 메시지 내용
     */
    public void sendErrorMessage(Long roomId, String errorMessage) {
        try {
            RoomStatusMessage message = new RoomStatusMessage();
            message.setType(RoomStatusMessage.MessageType.ERROR);
            message.setRoomId(roomId);
            message.setMessage(errorMessage);

            String destination = "/topic/rooms/" + roomId;
            messagingTemplate.convertAndSend(destination, message);

            log.error("Error message sent - Room ID: {}, Error: {}", roomId, errorMessage);
        } catch (Exception e) {
            // 에러 메시지 전송 실패 시 로그만 남기고 계속 진행
            log.error("Failed to send error message - Room ID: {}, Error message: {}, Send error: {}",
                    roomId, errorMessage, e.getMessage());
        }
    }
}
