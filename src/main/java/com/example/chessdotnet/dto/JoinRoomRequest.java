package com.example.chessdotnet.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class JoinRoomRequest {
    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;
}
