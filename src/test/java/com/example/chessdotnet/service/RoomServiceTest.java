package com.example.chessdotnet.service;

import com.example.chessdotnet.dto.RoomDTO;
import com.example.chessdotnet.entity.Room;
import com.example.chessdotnet.entity.User;
import com.example.chessdotnet.exception.UserNotFoundException;
import com.example.chessdotnet.repository.RoomRepository;
import com.example.chessdotnet.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * RoomService 클래스에 대한 단위 테스트를 수행합니다.
 * 방 생성, 참여, 퇴장 등의 핵심 기능을 테스트합니다.
 */
@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoomWebSocketService webSocketService;

    @InjectMocks
    private RoomService roomService;

    private User testUser;
    private Room testRoom;

    /**
     * 각 테스트 실행 전에 필요한 테스트 데이터를 초기화합니다.
     */
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");

        testRoom = new Room();
        testRoom.setId(1L);
        testRoom.setTitle("Test Room");
        testRoom.setHost(testUser);
        testRoom.setPlayersCount(1);
        testRoom.setMaxPlayers(2);
        testRoom.setPlayers(new HashSet<>(Collections.singletonList(testUser)));
    }

    /**
     * 새로운 방 생성 기능을 테스트합니다.
     */
    @Test
    @DisplayName("방 생성 테스트")
    void createRoom_ShouldCreateNewRoom() {
        // Given
        String title = "Test Room";
        Long hostId = 1L;
        when(userRepository.findById(hostId)).thenReturn(Optional.of(testUser));
        when(roomRepository.save(any(Room.class))).thenReturn(testRoom);

        // When
        RoomDTO result = roomService.createRoom(title, hostId);

        // Then
        assertNotNull(result);
        assertEquals(title, result.getTitle());
        assertEquals(hostId, result.getHostId());
        verify(roomRepository).save(any(Room.class));
    }

    /**
     * 존재하지 않는 사용자로 방 생성 시 예외 발생을 테스트합니다.
     */
    @Test
    @DisplayName("존재하지 않는 사용자로 방 생성 시 예외 발생")
    void createRoom_WithNonExistentUser_ShouldThrowException() {
        // Given
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () ->
                roomService.createRoom("Test Room", 999L)
        );
    }

    /**
     * 방 참여 기능을 테스트합니다.
     */
    @Test
    @DisplayName("방 참여 테스트")
    void joinRoom_ShouldAddPlayerToRoom() {
        // Given
        Long roomId = 1L;
        Long userId = 2L;
        User joiningUser = new User();
        joiningUser.setId(userId);
        joiningUser.setUsername("joiningUser");

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(testRoom));
        when(userRepository.findById(userId)).thenReturn(Optional.of(joiningUser));
        when(roomRepository.save(any(Room.class))).thenReturn(testRoom);

        // When
        RoomDTO result = roomService.joinRoom(roomId, userId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getPlayersCount());
        verify(webSocketService).notifyRoomStatusChanged(any(), any());
    }

    /**
     * 방이 가득 찼을 때 참여 시도 시 예외 발생을 테스트합니다.
     */
    @Test
    @DisplayName("가득 찬 방 참여 시 예외 발생")
    void joinRoom_WhenRoomIsFull_ShouldThrowException() {
        // Given
        testRoom.setPlayersCount(2);
        when(roomRepository.findById(any())).thenReturn(Optional.of(testRoom));
        when(userRepository.findById(any())).thenReturn(Optional.of(new User()));

        // When & Then
        assertThrows(RuntimeException.class, () ->
                roomService.joinRoom(1L, 2L)
        );
    }

    /**
     * 방 나가기 기능을 테스트합니다.
     */
    @Test
    @DisplayName("방 나가기 테스트 - 일반 사용자")
    void leaveRoom_ShouldRemovePlayerFromRoom() {
        // Given
        User normalUser = new User();
        normalUser.setId(2L);
        normalUser.setUsername("normalUser");
        testRoom.getPlayers().add(normalUser);
        testRoom.setPlayersCount(2);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));
        when(userRepository.findById(2L)).thenReturn(Optional.of(normalUser));
        when(roomRepository.save(any(Room.class))).thenReturn(testRoom);

        // When
        RoomDTO result = roomService.leaveRoom(1L, 2L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getPlayersCount());
        verify(roomRepository).save(any(Room.class));
    }

    /**
     * 방장이 나가고 다른 플레이어가 있을 때 호스트가 변경되는 것을 테스트합니다.
     */
    @Test
    @DisplayName("방장 나가기 테스트 - 다른 플레이어 있음")
    void leaveRoom_WhenHostLeavesWithOtherPlayers_ShouldChangeHost() {
        // Given
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setUsername("otherUser");
        testRoom.getPlayers().add(otherUser);
        testRoom.setPlayersCount(2);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(roomRepository.save(any(Room.class))).thenReturn(testRoom);

        // When
        RoomDTO result = roomService.leaveRoom(1L, 1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getPlayersCount());
        assertEquals(otherUser.getId(), result.getHostId());
    }

    /**
     * 마지막 플레이어(방장)가 나갈 때 방이 삭제되는 것을 테스트합니다.
     */
    @Test
    @DisplayName("마지막 플레이어(방장) 나가기 테스트 - 방 삭제")
    void leaveRoom_WhenLastPlayerLeaves_ShouldDeleteRoom() {
        // Given
        when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        RoomDTO result = roomService.leaveRoom(1L, 1L);

        // Then
        assertNull(result); // 방이 삭제되었으므로 null 반환
        verify(roomRepository).delete(testRoom);
    }

//    /**
//     * 게임 시작 기능을 테스트합니다.
//     * 아직 ChessGameService가 구현되지 않아 테스트가 불가능합니다.
//     */
//    @Test
//    @DisplayName("게임 시작 테스트")
//    void startGame_ShouldInitializeGame() {
//        // Given
//        testRoom.setGameStarted(true);
//        testRoom.setPlayersCount(2);
//        when(roomRepository.findById(1L)).thenReturn(Optional.of(testRoom));
//        when(roomRepository.save(any(Room.class))).thenReturn(testRoom);
//
//        // When
//        RoomDTO result = roomService.startGame(1L, new ArrayList<>());
//
//        // Then
//        assertNotNull(result);
//        assertTrue(result.isGameStarted());
//        assertNotNull(result.getIsHostWhitePlayer());
//        verify(webSocketService).notifyRoomStatusChanged(any(), eq(RoomStatusMessage.MessageType.GAME_STARTED));
//    }

    /**
     * 준비되지 않은 방에서 게임 시작 시도 시 예외 발생을 테스트합니다.
     */
    @Test
    @DisplayName("준비되지 않은 방 게임 시작 시 예외 발생")
    void startGame_WhenRoomNotReady_ShouldThrowException() {
        // Given
        testRoom.setGameStarted(false);
        when(roomRepository.findById(any())).thenReturn(Optional.of(testRoom));

        // When & Then
        assertThrows(IllegalStateException.class, () ->
                roomService.startGame(1L, new ArrayList<>())
        );
    }

    /**
     * 사용 가능한 방 목록 조회 기능을 테스트합니다.
     */
    @Test
    @DisplayName("사용 가능한 방 목록 조회 테스트")
    void getAvailableRooms_ShouldReturnAvailableRooms() {
        // Given
        when(roomRepository.findByIsGameStartedFalse())
                .thenReturn(Collections.singletonList(testRoom));

        // When
        List<RoomDTO> result = roomService.getAvailableRooms();

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testRoom.getTitle(), result.get(0).getTitle());
    }
}