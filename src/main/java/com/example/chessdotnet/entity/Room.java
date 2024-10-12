package com.example.chessdotnet.entity;

import com.example.chessdotnet.dto.RoomDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * 체스 게임 방을 나타내는 엔티티 클래스입니다.
 * 이 클래스는 방의 기본 정보, 생성자, 참여자 등을 관리합니다.
 *
 * @author 전종영
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

    /**
     * 방장의 체스 기물 색상 (true: 백, false: 흑)
     * null일 경우 아직 게임이 시작되지 않았음을 의미합니다.
     */
    @Column(nullable = true)
    private Boolean creatorColor;

    /** 현재 방에 참여한 플레이어 수, 기본값은 1 (방장) */
    @Column(nullable = false)
    private int currentPlayers = 1; // 현재 플레이어 수, 기본값 1

    /**
     * 게임 준비 상태
     * true일 경우 게임을 시작할 수 있는 상태입니다.
     */
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

    /**
     * 방 ID에 따라 방장의 체스 기물 색상을 설정합니다.
     */
    public void setCreatorColor() {
        if (this.id != null) {
            this.creatorColor = this.id % 2 == 0;
        }
    }

    /**
     * Room 엔티티를 RoomDTO로 변환합니다.
     *
     * @return 변환된 RoomDTO 객체
     */
    public RoomDTO toDTO() {
        RoomDTO dto = new RoomDTO();
        dto.setId(this.id);
        dto.setTitle(this.title);
        dto.setCreatorId(this.creator.getId());
        dto.setCreatorUsername(this.creator.getUsername());
        dto.setCurrentPlayers(this.currentPlayers);
        dto.setMaxPlayers(this.maxPlayers);
        dto.setGameStarted(this.isGameStarted);
        dto.setCreatorColor(this.creatorColor);
        return dto;
    }
}