package com.example.chessdotnet.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 체스 이동 명령을 전달하는 메인 DTO입니다.
 *
 * @author 전종영
 */
@Data
public class ChessMoveCommand {
    /** 시작 위치 */
    @NotNull
    private Position startPosition;

    /** 도착 위치 */
    @NotNull
    private Position endPosition;

    /** 기물 종류 */
    @NotNull
    private String pieceType;

    /** 특수 이동 정보 */
    private SpecialMoves specialMoves;
}
