package com.example.chessdotnet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 체스 게임에서 발생한 각각의 이동을 기록하는 엔티티 클래스입니다.
 * 이동한 기물의 정보, 시작 위치, 도착 위치, 특수 이동(캐슬링, 앙파상, 프로모션) 정보를 저장합니다.
 *
 * @author 전종영
 * @version 1.0
 * @since 2024-11-25
 */
@Entity
@Table(name = "chess_game_position")
@Getter @Setter
public class ChessGamePos {

    /**
     * 이동 기록의 고유 식별자입니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 이동이 발생한 게임입니다.
     */
    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private ChessGame game;

    /**
     * 이동 순서를 나타내는 번호입니다.
     */
    @Column(nullable = false)
    private Integer moveNumber;

    /**
     * 이동한 기물의 종류입니다. (예: PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING)
     */
    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private PieceType pieceType;

    /**
     * 이동한 기물의 색상입니다. (WHITE 또는 BLACK)
     */
    @Column(nullable = false, length = 5)
    @Enumerated(EnumType.STRING)
    private PieceColor pieceColor;

    /**
     * 시작 위치의 행 좌표입니다. (0-7)
     */
    @Column(nullable = false)
    private Integer startRow;

    /**
     * 시작 위치의 열 좌표입니다. (0-7)
     */
    @Column(nullable = false)
    private Integer startCol;

    /**
     * 도착 위치의 행 좌표입니다. (0-7)
     */
    @Column(nullable = false)
    private Integer endRow;

    /**
     * 도착 위치의 열 좌표입니다. (0-7)
     */
    @Column(nullable = false)
    private Integer endCol;

    /**
     * 캐슬링 여부를 나타냅니다.
     */
    @Column(nullable = false)
    private boolean isCastling = false;

    /**
     * 앙파상 여부를 나타냅니다.
     */
    @Column(nullable = false)
    private boolean isEnPassant = false;

    /**
     * 프로모션 발생 여부를 나타냅니다.
     */
    @Column(nullable = false)
    private boolean isPromotion = false;

    /**
     * 프로모션 시 변환된 기물의 종류입니다.
     * isPromotion이 true인 경우에만 값이 설정됩니다.
     */
    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private PieceType promotedTo;

    /**
     * 체스 기물의 종류를 나타내는 열거형입니다.
     */
    public enum PieceType {
        KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN
    }

    /**
     * 체스 기물의 색상을 나타내는 열거형입니다.
     */
    public enum PieceColor {
        WHITE, BLACK
    }
}