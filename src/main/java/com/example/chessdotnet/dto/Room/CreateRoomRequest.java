package com.example.chessdotnet.dto.Room;

import lombok.Data;

/**
 * 새로운 방 생성 요청을 위한 DTO(Data Transfer Object) 클래스입니다.
 *
 * @author 전종영
 * @TODO Validation 제약 조건
 */
@Data
public class CreateRoomRequest {
    private Long hostId; // 방 생성자 ID

    private Integer timeControlMin; // 타임 컨트롤 - 분

    private Integer timeControlSec; // 타임 컨트롤 - 초

    private Integer timeControlInc; // 타임 컨트롤 - 증초
}