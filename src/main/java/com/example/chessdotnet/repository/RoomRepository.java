package com.example.chessdotnet.repository;

import com.example.chessdotnet.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // 스프링의 데이터 접근 계층 컴포넌트임을 나타냄
public interface RoomRepository extends JpaRepository<Room, Long> {
    // 게임이 시작되지 않은 방들을 조회하는 메서드
    List<Room> findByIsGameStartedFalse();
}