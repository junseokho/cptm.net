package com.example.chessdotnet.dto.Room;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

/**
 * 게임 시작 요청 Request body 에 대한 DTO.
 */
@Data
public class StartGameRequest {
    /**
     * 게임을 시작하려는 방의 ID.
     */
    @NotNull
    private Long roomId;
    /**
     * 게임을 시작한 사용자의 ID.
     */
    @NotNull
    private Long userId;
}