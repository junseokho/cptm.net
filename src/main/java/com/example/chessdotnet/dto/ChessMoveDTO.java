package com.example.chessdotnet.dto;

import lombok.Data;

/**
 * 체스 기물 이동에 대한 요청 정보를 담는 DTO 클래스입니다.
 * 프론트엔드에서 전송하는 체스 기물 이동 정보를 매핑합니다.
 *
 * @author 전종영
 * @version 1.0
 * @since 2024-11-05
 */
@Data
public class ChessMoveDTO {
    /** 시작 위치 [x, y] */
    private int[] startPosition;

    /** 도착 위치 [x, y] */
    private int[] endPosition;

    /** 기물 종류 */
    private String pieceType;

    /** 특수 이동 정보 */
    private SpecialMoves specialMoves;

    /**
     * 특수 이동 정보를 담는 내부 클래스입니다.
     */
    @Data
    public static class SpecialMoves {
        /** 기물 잡기 여부 */
        private boolean takePiece;

        /** 잡은 기물 위치 [x, y] */
        private int[] takenPiecePosition;

        /** 앙파상 여부 */
        private boolean isEnpassant;

        /** 프로모션 여부 */
        private boolean promotion;

        /** 승격될 기물 종류 */
        private String promotionToWhat;

        /** 캐슬링 정보 */
        private Castling castling;
    }

    /**
     * 캐슬링 정보를 담는 내부 클래스입니다.
     */
    @Data
    public static class Castling {
        /** 킹사이드 캐슬링 여부 */
        private boolean isKingSide;
    }
}
