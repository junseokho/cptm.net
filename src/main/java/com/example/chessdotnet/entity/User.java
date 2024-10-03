package com.example.chessdotnet.entity;

import jakarta.persistence.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
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

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Room> getCreatedRooms() {
        return createdRooms;
    }

    public void setCreatedRooms(List<Room> createdRooms) {
        this.createdRooms = createdRooms;
    }

    public Set<Room> getJoinedRooms() {
        return joinedRooms;
    }

    public void setJoinedRooms(Set<Room> joinedRooms) {
        this.joinedRooms = joinedRooms;
    }
}
