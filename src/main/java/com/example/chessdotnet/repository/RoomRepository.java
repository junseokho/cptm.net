package com.example.chessdotnet.repository;

import com.example.chessdotnet.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Room 엔티티에 대한 데이터베이스 작업을 처리하는 리포지토리 인터페이스입니다.
 *
 * @author 전종영
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    /**
     * 게임이 시작되지 않은 모든 방을 조회합니다.
     *
     * @author 전종영
     * @return 게임이 시작되지 않은 Room 객체들의 List
     */
    List<Room> findByIsGameStartedFalse();
}