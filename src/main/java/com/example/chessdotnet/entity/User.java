package com.example.chessdotnet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @OneToMany(mappedBy = "creator")
    private List<Room> createdRooms;

    @ManyToMany(mappedBy = "players")
    private Set<Room> joinedRooms;

    // Lombok이 getter와 setter 메소드를 자동 생성하므로 기존의 메소드들은 제거합니다.
}