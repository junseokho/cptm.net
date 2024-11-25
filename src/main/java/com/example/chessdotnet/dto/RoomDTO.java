package com.example.chessdotnet.dto;

import lombok.Data;

/**
 * 방 정보를 전송하기 위한 데이터 전송 객체(DTO)입니다.
 *
 * @author 전종영
 */
@Data
public class RoomDTO {
    /** 방의 고유 식별자 */
    private Long id;

    /** 방 생성자의 ID */
    private Long hostId;

    /** 방 생성자의 이름 */
    private String hostUsername;

    /** 현재 방에 참여 중인 플레이어 수 */
    private int playersCount;

    /** 관전자 참여가능 여부 */
    private boolean canJoinAsSpectator;
}
