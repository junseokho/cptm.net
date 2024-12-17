package com.example.chessdotnet.repository;

import com.example.chessdotnet.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
     * 관전 가능한 모든 방을 조회합니다.
     *
     * @return 관전 가능한 Room 객체들의 List
     */
    @Query("SELECT r FROM Room r " +
            "WHERE NOT EXISTS (SELECT cg FROM ChessGame cg WHERE cg.room = r) " + // 아직 게임이 시작하지 않았거나
            "OR EXISTS (SELECT cg FROM ChessGame cg WHERE cg.room = r AND cg.playedEndTime IS NULL)") // 게임이 시작 되었지만 종료되지 않은 방
    List<Room> findSpectatableRooms();

    /**
     * 새로운 플레이어가 참여할 수 있는 방을 조회합니다.
     *
     * @return 참여 가능한 Room 객체들의 List
     */
    @Query("SELECT r FROM Room r WHERE r.joinedPlayer = NULL")
    List<Room> findPlayableRooms();

    /**
     * 방이 관전 가능한지 반환합니다.
     *
     * @return True if room is spectatable, False otherwise
     */
    @Query("SELECT CASE " +
            "WHEN COUNT(cg) = 0 THEN true " + // No ChessGame, then spectatable
            "ELSE (cg.playedEndTime IS NULL) " + // If ChessGame exists, it's spectatable if playedEndTime is null
            "END " +
            "FROM ChessGame cg " +
            "WHERE cg.room.id = :roomId")
    Boolean canJoinAsSpectator(@Param("roomId") Long roomId);
}