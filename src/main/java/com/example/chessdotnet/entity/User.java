package com.example.chessdotnet.entity;

import com.example.chessdotnet.dto.UserDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

/**
 * 사용자를 나타내는 엔티티 클래스입니다.
 * 이 클래스는 사용자의 기본 정보, 생성한 방, 참여한 방 등을 관리합니다.
 *
 * @author 전종영
 */
@Entity // JPA 엔티티임을 나타냄
@Table(name = "users") // 데이터베이스 테이블 이름 지정
@Getter @Setter // Lombok을 사용하여 getter와 setter 메소드 자동 생성
public class User {
    /** 사용자의 고유 식별자 */
    @Id // 기본 키 필드
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 생성 전략
    private Long id;

    /** 사용자의 고유한 이름 */
    @Column(nullable = false, unique = true)
    private String username; // 사용자 이름, 고유해야 함

    /** 사용자가 생성한 방 목록 */
    @OneToMany(mappedBy = "host") // 일대다 관계, Room 엔티티의 host 필드에 매핑
    private List<Room> createdRooms; // 사용자가 생성한 방 목록

    /** 사용자가 참여한 방 목록 */
    @ManyToMany(mappedBy = "players") // 다대다 관계, Room 엔티티의 players 필드에 매핑
    private Set<Room> joinedRooms; // 사용자가 참여한 방 목록

    /**
     * User 엔티티를 UserDTO로 변환합니다.
     *
     * @return 변환된 UserDTO 객체
     */
    public UserDTO toDTO() {
        UserDTO dto = new UserDTO();
        dto.setId(this.id);
        dto.setUsername(this.username);
        dto.setCreatedRoomsCount(this.createdRooms.size());
        dto.setJoinedRoomsCount(this.joinedRooms.size());
        return dto;
    }
}