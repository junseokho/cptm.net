package com.example.chessdotnet.dto.Room;

import lombok.Data;

/**
 * `Room` 정보를 클라이언트에게 전송하기 위한 DTO 입니다.
 *
 * @author 전종영
 * @apiNote 앞으로 필드가 추가될 수 있습니다.
 */
@Data
public class RoomDTO {
    /** 방의 고유 식별자 */
    private Long id;

    /** 방 생성자의 ID */
    private Long hostId;

    /** 방 생성자의 이름 */
    private String hostUsername;

    /** 방 생성자의 레이팅 */
    private Integer hostRating;


    /** 플레이어로 참가 가능 여부 */
    private Boolean canJoinAsPlayer;

    /** 관전 가능 여부 */
    private Boolean canJoinAsSpectator;

    /** 게임 종료 여부 */
    private Boolean isGameDone;

    /** 타임 컨트롤 - 분 */
    private Integer timeControlMin;

    /** 타임 컨트롤 - 초 */
    private Integer timeControlSec;

    /** 타임 컨트롤 - 증초 */
    private Integer timeControlInc;
}