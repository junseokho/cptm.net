package com.example.chessdotnet.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data // getter, setter, toString 등을 자동 생성
public class JoinRoomRequest {
    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId; // 방에 참여하려는 사용자 ID
}