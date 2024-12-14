package com.example.chessdotnet.dto.Room;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

/**
 * 방 참여 요청을 위한 DTO(Data Transfer Object) 클래스입니다.
 *
 * @author 전종영
 */
@Data // getter, setter, toString 등을 자동 생성
public class JoinRoomRequest {
    /**
     * 방에 참여하려는 사용자의 ID입니다.
     * 이 필드는 null이 될 수 없습니다.
     */
    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId; // 방에 참여하려는 사용자 ID
}