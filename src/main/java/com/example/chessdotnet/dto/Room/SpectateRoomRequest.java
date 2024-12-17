package com.example.chessdotnet.dto.Room;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 방 관전 요청 Request body 에 대한 DTO.
 */
@Data
public class SpectateRoomRequest {
    /**
     * 관전 요청 대상 방의 ID.
     */
    @NotNull
    private Long roomId;
    /**
     * 관전 요청을 보낸 사용자의 ID.
     */
    @NotNull
    private Long userId;
}
