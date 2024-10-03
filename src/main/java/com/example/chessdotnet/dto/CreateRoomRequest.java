package com.example.chessdotnet.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class CreateRoomRequest {
    @NotBlank(message = "방 제목은 필수입니다")
    @Size(min = 3, max = 50, message = "방 제목은 3자에서 50자 사이여야 합니다")
    private String title;

    @NotNull(message = "방장 ID는 필수입니다")
    private Long creatorId;
}


