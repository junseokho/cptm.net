package com.example.chessdotnet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 사용자 생성 요청을 위한 DTO(Data Transfer Object) 클래스입니다.
 *
 * @author 전종영
 */
@Data
public class CreateUserRequest {
    /**
     * 생성할 사용자의 이름입니다.
     * 이 필드는 비어있을 수 없으며, 3자에서 50자 사이여야 합니다.
     */
    @NotBlank(message = "사용자 이름은 필수입니다")
    @Size(min = 3, max = 50, message = "사용자 이름은 3자에서 50자 사이여야 합니다")
    private String username;
}
