package com.example.chessdotnet.repository;

import com.example.chessdotnet.entity.Room;
import com.example.chessdotnet.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RoomRepositorySpringTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoomRepository roomRepository;

    @Test
    void findByIsGameStartedFalse_ShouldReturnOnlyAvailableRooms() {
        // 먼저 User 엔티티를 생성하고 저장합니다.
        User user = new User();
        user.setUsername("testUser");
        entityManager.persist(user);

        Room availableRoom = new Room();
        availableRoom.setTitle("Available Room");
        availableRoom.setGameStarted(false);
        availableRoom.setCreator(user);  // creator 설정
        availableRoom.setMaxPlayers(2);  // maxPlayers 설정 (필요한 경우)
        availableRoom.setCurrentPlayers(1);  // currentPlayers 설정 (필요한 경우)
        entityManager.persist(availableRoom);

        Room startedRoom = new Room();
        startedRoom.setTitle("Started Room");
        startedRoom.setGameStarted(true);
        startedRoom.setCreator(user);  // creator 설정
        startedRoom.setMaxPlayers(2);  // maxPlayers 설정 (필요한 경우)
        startedRoom.setCurrentPlayers(2);  // currentPlayers 설정 (필요한 경우)
        entityManager.persist(startedRoom);

        entityManager.flush();

        List<Room> availableRooms = roomRepository.findByIsGameStartedFalse();

        assertThat(availableRooms).hasSize(1);
        assertThat(availableRooms.getFirst().getTitle()).isEqualTo("Available Room");
    }
}