package com.example.chessdotnet.repository;

import com.example.chessdotnet.entity.Room;
import com.example.chessdotnet.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * RoomRepository 인터페이스에 대한 단위 테스트입니다.
 */
@DataJpaTest
public class RoomRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoomRepository roomRepository;

    /**
     * findByIsGameStartedFalse 메서드가 게임이 시작되지 않은 방만 반환하는지 테스트합니다.
     */
    @Test
    void findByIsGameStartedFalse_ShouldReturnOnlyAvailableRooms() {
        User user = new User();
        user.setUsername("testUser");
        entityManager.persist(user);

        Room availableRoom = new Room();
        availableRoom.setTitle("Available Room");
        availableRoom.setGameStarted(false);
        availableRoom.setCreator(user);
        entityManager.persist(availableRoom);

        Room startedRoom = new Room();
        startedRoom.setTitle("Started Room");
        startedRoom.setGameStarted(true);
        startedRoom.setCreator(user);
        entityManager.persist(startedRoom);

        entityManager.flush();

        List<Room> availableRooms = roomRepository.findByIsGameStartedFalse();

        assertThat(availableRooms).hasSize(1);
        assertThat(availableRooms.get(0).getTitle()).isEqualTo("Available Room");
    }
}