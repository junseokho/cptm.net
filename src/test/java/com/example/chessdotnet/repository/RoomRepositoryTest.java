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
 * RoomRepository 인터페이스에 대한 단위 테스트입니다.
 *
 * @author 전종영
 */
@DataJpaTest
public class RoomRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoomRepository roomRepository;

    private User testUser;

    /**
     * 각 테스트 메서드 실행 전에 호출되어 테스트 환경을 설정합니다.
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
     * @param title 방 제목
     * @param isGameStarted 게임 시작 여부
     * @return 생성된 Room 엔티티
     */
    private Room createTestRoom(String title, boolean isGameStarted) {
        Room room = new Room();
        room.setTitle(title);
        room.setGameStarted(isGameStarted);
        room.setCreator(testUser);
        return entityManager.persist(room);
    }

    /**
     * findByIsGameStartedFalse 메서드가 게임이 시작되지 않은 방만 반환하는지 테스트합니다.
     */
    @Test
    void findByIsGameStartedFalse_ShouldReturnOnlyAvailableRooms() {
        createTestRoom("Available Room 1", false);
        createTestRoom("Available Room 2", false);
        createTestRoom("Started Room", true);

        entityManager.flush();

        List<Room> availableRooms = roomRepository.findByIsGameStartedFalse();

        assertThat(availableRooms).hasSize(2)
                .extracting(Room::getTitle)
                .containsExactlyInAnyOrder("Available Room 1", "Available Room 2");
    }

    /**
     * 모든 방을 조회하는 기능을 테스트합니다.
     */
    @Test
    void findAll_ShouldReturnAllRooms() {
        createTestRoom("Room 1", false);
        createTestRoom("Room 2", true);
        createTestRoom("Room 3", false);

        entityManager.flush();

        List<Room> allRooms = roomRepository.findAll();

        assertThat(allRooms).hasSize(3)
                .extracting(Room::getTitle)
                .containsExactlyInAnyOrder("Room 1", "Room 2", "Room 3");
    }

    /**
     * ID로 방을 조회하는 기능을 테스트합니다.
     */
    @Test
    void findById_ShouldReturnCorrectRoom() {
        Room savedRoom = createTestRoom("Test Room", false);

        entityManager.flush();

        Room foundRoom = roomRepository.findById(savedRoom.getId()).orElse(null);

        assertThat(foundRoom).isNotNull();
        assertThat(foundRoom.getTitle()).isEqualTo("Test Room");
        assertThat(foundRoom.isGameStarted()).isFalse();
    }
}