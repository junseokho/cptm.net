package com.example.chessdotnet;

import com.example.chessdotnet.controller.RoomController;
import com.example.chessdotnet.entity.Room;
import com.example.chessdotnet.entity.User;
import com.example.chessdotnet.repository.RoomRepository;
import com.example.chessdotnet.repository.UserRepository;
import com.example.chessdotnet.service.RoomService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ChessDotNet 애플리케이션에 대한 통합 테스트를 수행하는 클래스입니다.
 * 이 클래스는 애플리케이션의 주요 컴포넌트들이 올바르게 구성되고 작동하는지 확인합니다.
 * @author 전종영
 */
@SpringBootTest
@AutoConfigureMockMvc
class ChessDotNetIntegrationTest {

    @Autowired
    private RoomController roomController;

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    /**
     * 애플리케이션 컨텍스트가 올바르게 로드되는지 테스트합니다.
     * @author 전종영
     */
    @Test
    void contextLoads() {
        assertThat(roomController).isNotNull();
        assertThat(roomService).isNotNull();
        assertThat(roomRepository).isNotNull();
        assertThat(userRepository).isNotNull();
    }

    /**
     * RoomController의 getAvailableRooms 엔드포인트가 올바르게 작동하는지 테스트합니다.
     * @author 전종영
     * @throws Exception 테스트 실행 중 예외가 발생할 경우
     */
    @Test
    void testGetAvailableRooms() throws Exception {
        mockMvc.perform(get("/api/rooms/available"))
                .andExpect(status().isOk());
    }

    /**
     * UserRepository를 사용하여 사용자를 생성하고 조회할 수 있는지 테스트합니다.
     * @author 전종영
     */
    @Test
    void testUserRepositoryOperations() {
        long initialCount = userRepository.count();

        // 새 사용자 생성
        User newUser = new User();
        newUser.setUsername("testUser" + System.currentTimeMillis()); // 고유한 username 사용
        User savedUser = userRepository.save(newUser);

        // 사용자 수가 증가했는지 확인
        assertThat(userRepository.count()).isEqualTo(initialCount + 1);

        // 생성된 사용자를 조회할 수 있는지 확인
        assertThat(userRepository.findById(savedUser.getId())).isPresent();
    }

    /**
     * RoomRepository를 사용하여 방을 생성하고 조회할 수 있는지 테스트합니다.
     * @author 전종영
     */
    @Test
    void testRoomRepositoryOperations() {
        long initialCount = roomRepository.count();

        // 먼저 User 생성
        User creator = new User();
        creator.setUsername("roomCreator" + System.currentTimeMillis());
        creator = userRepository.save(creator);

        // 새 방 생성
        Room newRoom = new Room();
        newRoom.setTitle("Test Room");
        newRoom.setHost(creator);
        newRoom.setPlayersCount(1);
        newRoom.setMaxPlayers(2);
        newRoom.setGameStarted(false);
        Room savedRoom = roomRepository.save(newRoom);

        // 방 수가 증가했는지 확인
        assertThat(roomRepository.count()).isEqualTo(initialCount + 1);

        // 생성된 방을 조회할 수 있는지 확인
        assertThat(roomRepository.findById(savedRoom.getId())).isPresent();
    }
}