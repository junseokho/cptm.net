package com.example.chessdotnet.repository;

import com.example.chessdotnet.entity.Room;
import com.example.chessdotnet.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RoomRepository의 JPA 테스트 클래스입니다.
 *
 * @author 전종영
 */
@DataJpaTest
class RoomRepositorySpringTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoomRepository roomRepository;

    private User testUser;

    /**
     * 각 테스트 메서드 실행 전에 호출되어 테스트 환경을 설정합니다.
     *
     * @author 전종영
     */
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testUser");
        entityManager.persist(testUser);
    }

    /**
     * 테스트용 Room 엔티티를 생성합니다.
     *
     * @author 전종영
     * @param title 방 제목
     * @param isGameStarted 게임 시작 여부
     * @param maxPlayers 최대 플레이어 수
     * @param currentPlayers 현재 플레이어 수
     * @return 생성된 Room 엔티티
     */
    private Room createTestRoom(String title, boolean isGameStarted, int maxPlayers, int currentPlayers) {
        Room room = new Room();
        room.setTitle(title);
        room.setGameStarted(isGameStarted);
        room.setCreator(testUser);
        room.setMaxPlayers(maxPlayers);
        room.setCurrentPlayers(currentPlayers);
        return entityManager.persist(room);
    }

    /**
     * findByIsGameStartedFalse 메서드가 게임이 시작되지 않은 방만 반환하는지 테스트합니다.
     *
     * @author 전종영
     */
    @Test
    void findByIsGameStartedFalse_ShouldReturnOnlyAvailableRooms() {
        createTestRoom("Available Room 1", false, 2, 1);
        createTestRoom("Available Room 2", false, 2, 1);
        createTestRoom("Started Room", true, 2, 2);

        entityManager.flush();

        List<Room> availableRooms = roomRepository.findByIsGameStartedFalse();

        assertThat(availableRooms).hasSize(2)
                .extracting(Room::getTitle)
                .containsExactlyInAnyOrder("Available Room 1", "Available Room 2");
    }

    /**
     * 모든 방을 조회하는 기능을 테스트합니다.
     *
     * @author 전종영
     */
    @Test
    void findAll_ShouldReturnAllRooms() {
        createTestRoom("Room 1", false, 2, 1);
        createTestRoom("Room 2", true, 2, 2);
        createTestRoom("Room 3", false, 4, 2);

        entityManager.flush();

        List<Room> allRooms = roomRepository.findAll();

        assertThat(allRooms).hasSize(3)
                .extracting(Room::getTitle)
                .containsExactlyInAnyOrder("Room 1", "Room 2", "Room 3");
    }
}