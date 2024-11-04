package com.example.chessdotnet.dto;

import lombok.Data;

/**
 * 특수 이동에 대한 정보를 담는 DTO입니다.
 *
 * @author 전종영
 */
@Data
public class SpecialMoves {
    /** 기물 잡기 여부 */
    private boolean takePiece;

    /** 잡은 기물의 위치 */
    private Position takenPiecePosition;

    /** 앙파상 여부 */
    private boolean isEnpassant;

    /** 프로모션 여부 */
    private boolean promotion;

    /** 승격될 기물 종류 (예: "wQ"=백색 퀸) */
    private String promotionToWhat;

    /** 캐슬링 정보 */
    private Castling castling;
}
