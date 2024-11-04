package com.example.chessdotnet.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 체스 기물의 위치를 나타내는 DTO입니다.
 *
 * @author 전종영
 */
@Data
public class Position {
    /** x 좌표 (0-7) */
    @Min(0) @Max(7)
    private int x;

    /** y 좌표 (0-7) */
    @Min(0) @Max(7)
    private int y;
}
