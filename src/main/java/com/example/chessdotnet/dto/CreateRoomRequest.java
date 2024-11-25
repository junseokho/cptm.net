package com.example.chessdotnet.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 새로운 방 생성 요청을 위한 DTO(Data Transfer Object) 클래스입니다.
 * @author 전종영
 */
@Data // getter, setter, toString 등을 자동 생성
public class CreateRoomRequest {
    /**
     * 방을 생성하는 사용자의 ID입니다.
     * 이 필드는 null이 될 수 없습니다.
     */
    @NotNull(message = "방장 ID는 필수입니다")
    private Long hostId; // 방 생성자 ID

}