package com.example.chessdotnet.entity;

import com.example.chessdotnet.dto.UserDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 사용자를 나타내는 엔티티 클래스입니다.
 * 이 클래스는 사용자의 기본 정보, 생성한 방, 참여한 방 등을 관리합니다.
 *
 * @author 전종영
 */
@Entity // JPA 엔티티임을 나타냄
@Table(name = "user") // 데이터베이스 테이블 이름 지정
@Getter @Setter // Lombok을 사용하여 getter와 setter 메소드 자동 생성
public class User {
    /** 사용자의 고유 식별자 */
    @Id // 기본 키 필드
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 생성 전략
    private Long id;

    /** 사용자의 고유한 이름 */
    @Column(nullable = false, unique = true)
    private String username; // 사용자 이름, 고유해야 함

    /**
     * 사용자의 레이팅 점수입니다.
     * 기본값은 1000입니다.
     */
    @Column(nullable = false)
    private int rating = 1000; // 사용자의 레이팅

    /**
     * User 엔티티를 UserDTO로 변환합니다.
     *
     * @return 변환된 UserDTO 객체
     */
    public UserDTO toDTO() {
        UserDTO dto = new UserDTO();
        dto.setId(this.id);
        dto.setUsername(this.username);
        dto.setRating(this.rating);
        return dto;
    }
}