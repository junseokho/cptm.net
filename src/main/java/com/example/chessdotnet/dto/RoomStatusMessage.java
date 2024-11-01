package com.example.chessdotnet.dto;

import lombok.Data;

/**
 * 방 상태 변경에 대한 메시지를 정의하는 DTO 클래스입니다.
 * 이 클래스는 웹소켓을 통해 클라이언트와 서버 간에 주고받는 메시지의 형식을 정의합니다.
 *
 * @author 전종영
 * @version 1.0
 * @since 2024-11-01
 */
@Data
public class RoomStatusMessage {
    /**
     * 방 상태 변경 메시지의 타입을 정의하는 열거형입니다.
     * 각 타입은 특정한 방 상태 변경 이벤트를 나타냅니다.
     */
    public enum MessageType {
        /** 게임을 시작할 수 있는 상태가 되었음을 나타냅니다. */
        ROOM_READY,
        /** 게임이 실제로 시작되었음을 나타냅니다. */
        GAME_STARTED,
        /** 새로운 플레이어가 방에 입장했음을 나타냅니다. */
        PLAYER_JOINED,
        /** 플레이어가 방에서 나갔음을 나타냅니다. */
        PLAYER_LEFT,
        /** 오류가 발생했음을 나타냅니다. */
        ERROR
    }

    /** 메시지의 타입입니다. */
    private MessageType type;

    /** 대상 방의 고유 식별자입니다. */
    private Long roomId;

    /** 방의 현재 상태 정보를 담고 있는 DTO입니다. */
    private RoomDTO roomDTO;

    /** 추가적인 메시지 내용입니다. 주로 에러 메시지에 사용됩니다. */
    private String message;
}