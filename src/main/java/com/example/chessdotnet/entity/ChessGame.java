package com.example.chessdotnet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * 체스 게임의 상태와 진행 정보를 관리하는 엔티티 클래스입니다.
 * 게임의 진행 기록, 플레이어 정보, 시작/종료 시간 등을 저장합니다.
 *
 * @author 전종영
 * @version 2.1
 * @since 2024-11-25
 */
@Entity
@Table(name = "chess_games")
@Getter
@Setter
public class ChessGame {

    /**
     * 게임의 고유 식별자입니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 게임이 진행되는 방입니다.
     * Room 엔티티와의 다대일 관계를 표현합니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    /**
     * 게임의 이동 기록을 저장하는 리스트입니다.
     * 각 이동은 ChessGamePos 엔티티로 표현됩니다.
     */
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("moveNumber ASC")
    private List<ChessGamePos> moveRecords = new ArrayList<>();

    /**
     * 게임 시작 시간입니다.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date playedStartTime;

    /**
     * 게임 종료 시간입니다.
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date playedEndTime;

    /**
     * 백색 기물을 플레이하는 사용자의 ID입니다.
     */
    @Column(nullable = false)
    private Long whitePlayerId;

    /**
     * 새로운 체스 게임을 생성하는 생성자입니다.
     *
     * @param room          게임이 진행될 방
     * @param whitePlayerId 백색 기물을 플레이할 사용자의 ID
     */
    public ChessGame(Room room, Long whitePlayerId) {
        this.room = room;
        this.whitePlayerId = whitePlayerId;
        this.playedStartTime = new Date();
    }

    /**
     * JPA를 위한 기본 생성자입니다.
     */
    protected ChessGame() {
    }

    /**
     * 게임을 종료 처리합니다.
     */
    public void endGame() {
        this.playedEndTime = new Date();
    }

    /**
     * 새로운 이동을 기록에 추가합니다.
     *
     * @param moveRecord 추가할 이동 기록
     */
    public void addMoveRecord(ChessGamePos moveRecord) {
        moveRecords.add(moveRecord);
        moveRecord.setGame(this);
    }

    /**
     * 현재 게임이 진행 중인 방의 ID를 반환합니다.
     *
     * @return 방 ID
     */
    public Long getRoomId() {
        return room != null ? room.getId() : null;
    }


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