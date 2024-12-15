package com.example.chessdotnet.dto.Room;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

/**
 * 방 참여 요청 Request body 에 대한 DTO.
 *
 * @author 전종영
 */
@Data
public class JoinRoomRequest {
    /**
     * 참여하려는 방의 ID.
     */
    @NotNull
    private Long roomId;
    /**
     * 방에 참여하려는 사용자의 ID.
     */
    @NotNull
    private Long userId;
}