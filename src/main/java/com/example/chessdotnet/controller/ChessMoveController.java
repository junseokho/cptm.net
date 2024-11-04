package com.example.chessdotnet.controller;

import com.example.chessdotnet.dto.*;
import com.example.chessdotnet.service.ChessMoveService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import jakarta.validation.Valid;

/**
 * WebSocket을 통한 체스 게임 이동을 처리하는 컨트롤러입니다.
 *
 * @author 전종영
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ChessMoveController {

    private final ChessMoveService chessMoveService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 체스 기물 이동 요청을 처리합니다.
     *
     * @param roomId 게임이 진행중인 방 ID
     * @param moveCommand 이동 명령 정보
     */
    @MessageMapping("/rooms/{roomId}/chess/move")
    public void handleMove(
            @DestinationVariable Long roomId,
            @Valid @Payload ChessMoveCommand moveCommand) {

        log.info("Chess move received - Room ID: {}, Move: {} to {}",
                roomId, moveCommand.getStartPosition(), moveCommand.getEndPosition());

        try {
            // 이동 실행 및 결과 얻기
            ChessMoveResult result = chessMoveService.executeMove(roomId, moveCommand);

            // 결과를 해당 방의 모든 참가자에게 브로드캐스트
            messagingTemplate.convertAndSend(
                    "/topic/rooms/" + roomId + "/chess/state",
                    result
            );

            // 게임이 종료되었다면 추가 메시지 전송
            if (result.isCheckmate()) {
                RoomStatusMessage gameEndMessage = new RoomStatusMessage();
                gameEndMessage.setType(RoomStatusMessage.MessageType.GAME_ENDED);
                gameEndMessage.setRoomId(roomId);
                gameEndMessage.setMessage("Checkmate! Game Over!");

                messagingTemplate.convertAndSend(
                        "/topic/rooms/" + roomId,
                        gameEndMessage
                );
            }

        } catch (Exception e) {
            log.error("Error processing chess move", e);

            // 에러 발생 시 에러 메시지 전송
            ChessMoveResult errorResult = new ChessMoveResult();
            errorResult.setSuccess(false);
            errorResult.setErrorMessage(e.getMessage());

            messagingTemplate.convertAndSend(
                    "/topic/rooms/" + roomId + "/chess/state",
                    errorResult
            );
        }
    }
}