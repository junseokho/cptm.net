package com.example.chessdotnet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * 체스 게임 방을 나타내는 엔티티 클래스입니다.
 * 이 클래스는 방의 기본 정보, 생성자, 참여자 등을 관리합니다.
 */
@Entity // JPA 엔티티임을 나타냄
@Table(name = "rooms") // 데이터베이스 테이블 이름 지정
@Getter @Setter // Lombok을 사용하여 getter와 setter 메소드 자동 생성
public class Room {
    /** 방의 고유 식별자 */
    @Id // 기본 키 필드
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 생성 전략
    private Long id;

    /** 방 제목 */
    @Column(nullable = false)
    private String title; // 방 제목

    /** 최대 플레이어 수, 기본값은 2 */
    @Column(nullable = false)
    private int maxPlayers = 2; // 최대 플레이어 수, 기본값 2

    /** 현재 방에 참여한 플레이어 수, 기본값은 1 (방장) */
    @Column(nullable = false)
    private int currentPlayers = 1; // 현재 플레이어 수, 기본값 1

    /** 게임 시작 여부 */
    @Column(nullable = false)
    private boolean isGameStarted = false; // 게임 시작 여부

    /** 방을 생성한 사용자 */
    @ManyToOne(fetch = FetchType.LAZY) // 다대일 관계, 지연 로딩
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator; // 방 생성자

    /** 방에 참여한 플레이어들 */
    @ManyToMany(fetch = FetchType.LAZY) // 다대다 관계, 지연 로딩
    @JoinTable(
            name = "room_players", // 연결 테이블 이름
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> players = new HashSet<>(); // 방에 참여한 플레이어들
}