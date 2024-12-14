package com.example.chessdotnet.dto.Room;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

/**
 * 방을 나가는 요청에 대한 데이터 전송 객체(DTO)입니다.
 *
 * @author 전종영
 */
@Data
public class LeaveRoomRequest {
    /**
     * 방을 나가려는 사용자의 ID입니다.
     * 이 필드는 null이 될 수 없습니다.
     */
    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;
}