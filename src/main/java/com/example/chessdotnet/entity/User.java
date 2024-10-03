package com.example.chessdotnet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Entity // JPA 엔티티임을 나타냄
@Table(name = "users") // 데이터베이스 테이블 이름 지정
@Getter @Setter // Lombok을 사용하여 getter와 setter 메소드 자동 생성
public class User {
    @Id // 기본 키 필드
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 생성 전략
    private Long id;

    @Column(nullable = false, unique = true)
    private String username; // 사용자 이름, 고유해야 함

    @OneToMany(mappedBy = "creator") // 일대다 관계, Room 엔티티의 creator 필드에 매핑
    private List<Room> createdRooms; // 사용자가 생성한 방 목록

    @ManyToMany(mappedBy = "players") // 다대다 관계, Room 엔티티의 players 필드에 매핑
    private Set<Room> joinedRooms; // 사용자가 참여한 방 목록
}