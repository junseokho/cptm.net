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

    /** 현재 방에 참여한 플레이어 수, 기본값은 1 (방장)
     * playersCount = 2가 되면 게임 시작 가능
     * */
    @Column(nullable = false)
    private int playersCount = 1; // 현재 플레이어 수, 기본값 1

    /** 관전자 참여가능 여부 */
    @Column(nullable = false)
    private boolean canJoinAsSpectator = false; // 관전자로 참여 가능 여부

    /** 방을 생성한 사용자 */
    @ManyToOne(fetch = FetchType.LAZY) // 다대일 관계, 지연 로딩
    @JoinColumn(name = "host_id", nullable = false)
    private User host; // 방 생성자

    /** 방에 참여한 플레이어 */
    @ManyToOne(fetch = FetchType.LAZY) // 다대일 관계, 지연 로딩
    @JoinColumn(name = "player_id", nullable = false)
    private User joinedPlayer; // 방에 참여한 플레이어

    /**
     * Room 엔티티를 RoomDTO로 변환합니다.
     *
     * @return 변환된 RoomDTO 객체
     */
    public RoomDTO toDTO() {
        RoomDTO dto = new RoomDTO();
        dto.setId(this.id);
        dto.setHostId(this.host.getId());
        dto.setHostUsername(this.host.getUsername());
        dto.setPlayersCount(this.playersCount);
        dto.setCanJoinAsSpectator(this.canJoinAsSpectator);
        return dto;
    }
}