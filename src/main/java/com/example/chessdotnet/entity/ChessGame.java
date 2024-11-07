package com.example.chessdotnet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;

/**
 * 체스 게임의 상태를 관리하는 엔티티 클래스입니다.
 * 게임의 진행 상태, 보드 상태, 턴 정보 등을 저장합니다.
 *
 * @author 전종영
 * @version 1.1
 * @since 2024-11-05
 */
@Entity
@Table(name = "chess_games")
@Getter @Setter
public class ChessGame {
    /**
     * 게임의 고유 식별자입니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 게임이 진행되는 방입니다.
     */
    @OneToOne
    @JoinColumn(name = "room_id")
    private Room room;

    /**
     * 현재 턴의 플레이어 색상입니다 (true: white, false: black).
     */
    @Column(name = "white_turn")
    private boolean whiteTurn = true;

    /**
     * 게임 상태입니다.
     */
    @Enumerated(EnumType.STRING)
    private GameStatus status = GameStatus.IN_PROGRESS;

    /**
     * 체크 상태 여부입니다.
     */
    private boolean isCheck = false;

    /**
     * 체스 보드의 현재 상태입니다 (JSON 형식).
     * 형식: {"pieces": {"row,col": "pieceCode"}}
     * pieceCode 예: "wP" (white pawn), "bK" (black king)
     */
    @Column(columnDefinition = "TEXT")
    private String boardState;

    /**
     * 마지막 이동 시간입니다.
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastMoveTime;

    /**
     * 게임 시작 시간입니다.
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime = new Date();

    /**
     * 게임 종료 시간입니다.
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;

    /**
     * 게임의 상태를 나타내는 열거형입니다.
     */
    public enum GameStatus {
        /**
         * 게임 진행 중
         */
        IN_PROGRESS,

        /**
         * 체크 상태
         */
        CHECK,

        /**
         * 체크메이트 (게임 종료)
         */
        CHECKMATE,

        /**
         * 스테일메이트 (무승부)
         */
        STALEMATE,

        /**
         * 무승부
         */
        DRAW
    }
}