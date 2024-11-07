package com.example.chessdotnet.dto;

import lombok.Data;

/**
 * 체스 기물 이동에 대한 요청 정보를 담는 DTO 클래스입니다.
 * React 프론트엔드와의 통신을 위한 데이터 구조를 정의합니다.
 *
 * @author 전종영
 * @version 1.1
 * @since 2024-11-05
 */
@Data
public class ChessMoveDTO {
    /**
     * 이동할 기물의 정보입니다.
     */
    private PieceInfo piece;

    /**
     * 시작 위치 [row, col]입니다.
     */
    private Position startPosition;

    /**
     * 도착 위치 [row, col]입니다.
     */
    private Position endPosition;

    /**
     * 특수 이동 정보입니다.
     */
    private SpecialMoves specialMoves;

    /**
     * 체스 기물의 정보를 담는 내부 클래스입니다.
     */
    @Data
    public static class PieceInfo {
        /**
         * 기물의 이름입니다 (예: "pawn", "knight" 등).
         */
        private String name;

        /**
         * 기물의 색상입니다 ("white" 또는 "black").
         */
        private String color;
    }

    /**
     * 체스 기물의 위치를 나타내는 내부 클래스입니다.
     */
    @Data
    public static class Position {
        /**
         * 행 위치 (0-7)입니다.
         */
        private int row;

        /**
         * 열 위치 (0-7)입니다.
         */
        private int col;
    }

    /**
     * 특수 이동 정보를 담는 내부 클래스입니다.
     */
    @Data
    public static class SpecialMoves {
        /**
         * 기물 잡기 여부입니다.
         */
        private boolean takePiece;

        /**
         * 잡은 기물의 위치입니다.
         */
        private Position takenPiecePosition;

        /**
         * 앙파상 여부입니다.
         */
        private boolean isEnpassant;

        /**
         * 폰 승급 여부입니다.
         */
        private boolean promotion;

        /**
         * 승급할 기물 종류입니다.
         */
        private String promotionToWhat;

        /**
         * 캐슬링 정보입니다.
         */
        private Castling castling;
    }

    /**
     * 캐슬링 정보를 담는 내부 클래스입니다.
     */
    @Data
    public static class Castling {
        /**
         * 킹사이드 캐슬링 여부입니다.
         */
        private boolean isKingSide;
    }
}