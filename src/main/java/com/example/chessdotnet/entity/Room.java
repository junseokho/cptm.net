package com.example.chessdotnet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


/**
 * 체스 게임 방을 나타내는 엔티티 클래스입니다.
 * 이 클래스는 방의 기본 정보, 생성자, 참여자 등을 관리합니다.
 *
 * @author 전종영
 */
@Entity
@Table(name = "room")
@Getter
@Setter
public class Room {
    /**
     * The unique identifier for a room.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The player who created the room.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "host_player", referencedColumnName = "id")
    private User hostPlayer;

    /**
     * The player who joined the room. Can be null if no player has joined yet.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "joined_player", referencedColumnName = "id")
    private User joinedPlayer;

    /**
     * The time control in minutes for the chess game.
     */
    private int timeControlMin;

    /**
     * The time control in seconds for the chess game.
     */
    private int timeControlSec;

    /**
     * The increment in seconds for each move in the chess game.
     */
    private int timeControlInc;
}