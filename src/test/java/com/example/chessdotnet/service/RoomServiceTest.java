package com.example.chessdotnet.service;

import com.example.chessdotnet.dto.RoomDTO;
import com.example.chessdotnet.entity.Room;
import com.example.chessdotnet.entity.User;
import com.example.chessdotnet.exception.RoomNotFoundException;
import com.example.chessdotnet.exception.UserNotFoundException;
import com.example.chessdotnet.repository.RoomRepository;
import com.example.chessdotnet.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * RoomService 클래스에 대한 단위 테스트입니다.
 * 이 테스트 클래스는 RoomService의 주요 기능들이 예상대로 동작하는지 확인합니다.
 *
 * @author 전종영
 */
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RoomService roomService;

    /**
     * 각 테스트 메소드 실행 전에 호출되어 테스트 환경을 설정합니다.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * createRoom 메서드가 정상적으로 방을 생성하는지 테스트합니다.
     * 방 생성 시 제목과 생성자 정보가 올바르게 설정되는지 확인합니다.
     */
    @Test
    void createRoom_ShouldCreateRoom() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> {
            Room savedRoom = invocation.getArgument(0);
            savedRoom.setId(1L);
            return savedRoom;
        });

        // When
        RoomDTO roomDTO = roomService.createRoom("Test Room", 1L);

        // Then
        assertNotNull(roomDTO);
        assertEquals("Test Room", roomDTO.getTitle());
        assertEquals(1L, roomDTO.getHostId());
        assertEquals("testUser", roomDTO.getHostUsername());
        verify(roomRepository).save(any(Room.class));
    }

    /**
     * createRoom 메서드가 존재하지 않는 사용자로 인해 예외를 던지는지 테스트합니다.
     * UserNotFoundException이 발생하는지 확인합니다.
     */
    @Test
    void createRoom_ShouldThrowUserNotFoundException() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UserNotFoundException.class, () -> roomService.createRoom("Test Room", 1L));
    }

    /**
     * joinRoom 메서드가 정상적으로 사용자를 방에 참여시키는지 테스트합니다.
     * 방에 사용자가 추가되고 현재 플레이어 수가 증가하는지 확인합니다.
     */
    @Test
    void joinRoom_ShouldAddUserToRoom() {
        // Given
        User host = new User();
        host.setId(1L);
        host.setUsername("hostUser");

        Room room = new Room();
        room.setId(1L);
        room.setTitle("Test Room");
        room.setMaxPlayers(2);
        room.setPlayersCount(1);
        room.setHost(host);

        User joiningUser = new User();
        joiningUser.setId(2L);
        joiningUser.setUsername("joinUser");

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(userRepository.findById(2L)).thenReturn(Optional.of(joiningUser));
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        RoomDTO updatedRoomDTO = roomService.joinRoom(1L, 2L);

        // Then
        assertNotNull(updatedRoomDTO);
        assertEquals(2, updatedRoomDTO.getPlayersCount());
        assertEquals(1L, updatedRoomDTO.getHostId());
        assertEquals("hostUser", updatedRoomDTO.getHostUsername());
        verify(roomRepository).save(any(Room.class));
    }

    /**
     * joinRoom 메서드가 존재하지 않는 방으로 인해 예외를 던지는지 테스트합니다.
     * RoomNotFoundException이 발생하는지 확인합니다.
     */
    @Test
    void joinRoom_ShouldThrowRoomNotFoundException() {
        // Given
        when(roomRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RoomNotFoundException.class, () -> roomService.joinRoom(1L, 2L));
    }

    /**
     * getAvailableRooms 메서드가 사용 가능한 방 목록을 반환하는지 테스트합니다.
     * 게임이 시작되지 않은 방들만 반환되는지 확인합니다.
     */
    @Test
    void getAvailableRooms_ShouldReturnAvailableRooms() {
        // Given
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");

        Room room1 = new Room();
        room1.setId(1L);
        room1.setTitle("Room 1");
        room1.setHost(user1);

        Room room2 = new Room();
        room2.setId(2L);
        room2.setTitle("Room 2");
        room2.setHost(user2);

        when(roomRepository.findByIsGameStartedFalse()).thenReturn(Arrays.asList(room1, room2));

        // When
        List<RoomDTO> availableRooms = roomService.getAvailableRooms();

        // Then
        assertEquals(2, availableRooms.size());
        assertEquals("Room 1", availableRooms.get(0).getTitle());
        assertEquals("user1", availableRooms.get(0).getHostUsername());
        assertEquals("Room 2", availableRooms.get(1).getTitle());
        assertEquals("user2", availableRooms.get(1).getHostUsername());
        verify(roomRepository).findByIsGameStartedFalse();
    }
}
