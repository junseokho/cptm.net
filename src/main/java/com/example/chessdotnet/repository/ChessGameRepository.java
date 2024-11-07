package com.example.chessdotnet.repository;

import com.example.chessdotnet.entity.ChessGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * ChessGame 엔티티에 대한 데이터베이스 작업을 처리하는 리포지토리 인터페이스입니다.
 *
 * @author 전종영
 * @version 1.0
 * @since 2024-11-05
 */
@Repository
public interface ChessGameRepository extends JpaRepository<ChessGame, Long> {
    /**
     * 특정 방의 진행 중인 게임을 찾습니다.
     *
     * @param roomId 방 ID
     * @param status 게임 상태
     * @return 해당 방의 진행 중인 게임 (Optional)
     */
    Optional<ChessGame> findByRoom_IdAndStatus(Long roomId, ChessGame.GameStatus status);
}