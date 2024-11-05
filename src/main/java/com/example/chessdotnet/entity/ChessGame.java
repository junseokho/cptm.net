package com.example.chessdotnet.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

/**
 * 체스 게임의 상태를 관리하는 엔티티 클래스입니다.
 *
 * @author 전종영
 * @version 1.0
 * @since 2024-11-05
 */
@Entity
@Table(name = "chess_games")
@Getter
@Setter
public class ChessGame {
    /** 게임의 고유 식별자 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 게임이 진행되는 방 */
    @OneToOne
    @JoinColumn(name = "room_id")
    private Room room;

    /** 현재 턴의 플레이어 색상 (true: 백, false: 흑) */
    @Column(name = "white_turn")
    private boolean whiteTurn = true;

    /** 게임 상태 (진행 중, 종료 등) */
    @Enumerated(EnumType.STRING)
    private GameStatus status = GameStatus.IN_PROGRESS;

    /** 체크 상태 여부 */
    private boolean isCheck = false;

    /** 게임 보드의 현재 상태 (JSON 형식으로 저장) */
    @Column(columnDefinition = "TEXT")
    private String boardState;

    /** 마지막 이동 시간 */
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastMoveTime;

    /** 게임 시작 시간 */
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime = new Date();

    /** 게임 종료 시간 */
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;

    /**
     * 게임의 현재 상태를 나타내는 열거형입니다.
     */
    public enum GameStatus {
        IN_PROGRESS,
        CHECK,
        CHECKMATE,
        STALEMATE,
        DRAW
    }

    /**
     * 현재 턴이 백의 차례인지 확인합니다.
     *
     * @return 백의 차례이면 true, 흑의 차례이면 false
     */
    public boolean isWhiteTurn() {
        return whiteTurn;
    }

    /**
     * 현재 턴을 설정합니다.
     *
     * @param whiteTurn 백의 차례이면 true, 흑의 차례이면 false
     */
    public void setWhiteTurn(boolean whiteTurn) {
        this.whiteTurn = whiteTurn;
    }
}
