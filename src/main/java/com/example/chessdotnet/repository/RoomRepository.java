package com.example.chessdotnet.repository;

import com.example.chessdotnet.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Room 엔티티에 대한 데이터베이스 작업을 처리하는 리포지토리 인터페이스입니다.
 *
 * @author 전종영
 * @version 1.3
 * @since 2024-11-25
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    /**
     * 새로운 플레이어가 참여 가능한 방을 조회합니다.
     *
     * @param count 현재 플레이어 수
     * @return 참여 가능한 Room 객체들의 List
     */
    List<Room> findByPlayersCount(int count);

    /**
     * 관전 가능한 모든 방을 조회합니다.
     * 관전이 허용되고 플레이어가 2명인 방을 반환합니다.
     *
     * @return 관전 가능한 Room 객체들의 List
     */
    @Query("SELECT r FROM Room r WHERE r.canJoinAsSpectator = true AND r.playersCount = 2 ORDER BY r.id DESC")
    List<Room> findSpectateableRooms();

    /**
     * 새로운 플레이어가 참여할 수 있는 방을 조회합니다.
     * playersCount가 1이면서 관전 모드가 아닌 방을 반환합니다.
     *
     * @return 참여 가능한 Room 객체들의 List
     */
    @Query("SELECT r FROM Room r WHERE r.playersCount = 1 AND r.canJoinAsSpectator = false ORDER BY r.id DESC")
    List<Room> findJoinableRooms();
}