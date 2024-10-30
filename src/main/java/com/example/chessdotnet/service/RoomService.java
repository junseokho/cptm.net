package com.example.chessdotnet.service;

import com.example.chessdotnet.dto.RoomDTO;
import com.example.chessdotnet.entity.Room;
import com.example.chessdotnet.entity.User;
import com.example.chessdotnet.exception.RoomNotFoundException;
import com.example.chessdotnet.exception.UserNotFoundException;
import com.example.chessdotnet.exception.UserNotInRoomException;
import org.springframework.stereotype.Service;
import com.example.chessdotnet.repository.RoomRepository;
import com.example.chessdotnet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 체스 게임 방 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 *
 * @author 전종영
 */
@Service // 비즈니스 로직을 담당하는 서비스 클래스임을 나타냄
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebSocketService webSocketService;

    /**
     * 새로운 방을 생성합니다.
     *
     * @param title 생성할 방의 제목
     * @param hostId 방을 생성하는 사용자의 ID
     * @return 생성된 Room의 DTO
     * @throws UserNotFoundException 지정된 ID의 사용자를 찾을 수 없는 경우
     */
    public RoomDTO createRoom(String title, Long hostId) {
        User host = userRepository.findById(hostId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Room room = new Room();
        room.setTitle(title);
        room.setHost(host);
        room.getPlayers().add(host);
        Room savedRoom = roomRepository.save(room);
        return savedRoom.toDTO();
    }

    /**
     * 사용자가 특정 게임 방에 참여합니다.
     *
     * @param roomId 참여할 방의 ID
     * @param userId 참여하는 사용자의 ID
     * @return 업데이트된 Room의 DTO
     * @throws RoomNotFoundException 방을 찾을 수 없을 때 발생
     * @throws UserNotFoundException 사용자를 찾을 수 없을 때 발생
     */
    public RoomDTO joinRoom(Long roomId, Long userId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // 방이 가득 찼는지 확인
        if (room.getPlayersCount() >= room.getMaxPlayers()) {
            throw new RuntimeException("Room is full");
        }

        // 사용자를 방에 추가
        room.getPlayers().add(user);
        room.setPlayersCount(room.getPlayersCount() + 1);

        // 방이 가득 찼다면 게임 시작 상태로 변경
        if (room.getPlayersCount() == room.getMaxPlayers()) {
            room.setGameStarted(true);
        }

        Room updatedRoom = roomRepository.save(room);
        return updatedRoom.toDTO();
    }

    /**
     * 사용 가능한 (게임이 시작되지 않은) 모든 방의 목록을 반환합니다.
     *
     * @return 사용 가능한 방들의 DTO 리스트
     */
    public List<RoomDTO> getAvailableRooms() {
        return roomRepository.findByIsGameStartedFalse().stream()
                .map(Room::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 사용자가 방을 나가는 기능을 처리합니다.
     *
     * @param roomId 나가려는 방의 ID
     * @param userId 나가려는 사용자의 ID
     * @return 업데이트된 방 정보. 방이 삭제된 경우 null을 반환
     * @throws RoomNotFoundException 지정된 ID의 방을 찾을 수 없는 경우
     * @throws UserNotFoundException 지정된 ID의 사용자를 찾을 수 없는 경우
     * @throws UserNotInRoomException 사용자가 해당 방에 없는 경우
     */
    @Transactional
    public RoomDTO leaveRoom(Long roomId, Long userId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!room.getPlayers().contains(user)) {
            throw new UserNotInRoomException("User is not in the room");
        }

        room.getPlayers().remove(user);
        room.setPlayersCount(room.getPlayersCount() - 1);

        // 게임이 시작되었고 플레이어가 1명 이하가 되면 게임 종료
        if (room.isGameStarted() && room.getPlayersCount() <= 1) {
            room.setGameStarted(false);
        }

        if (room.getHost().equals(user)) {
            if (room.getPlayers().isEmpty()) {
                roomRepository.delete(room);
                return null;
            } else {
                room.setHost(room.getPlayers().iterator().next());
            }
        }

        Room updatedRoom = roomRepository.save(room);
        return updatedRoom.toDTO();
    }

    /**
     * 방을 삭제하는 기능을 처리합니다.
     *
     * @param roomId 삭제할 방의 ID
     * @param userId 삭제를 요청한 사용자의 ID
     * @throws RoomNotFoundException 지정된 ID의 방을 찾을 수 없는 경우
     * @throws UserNotFoundException 지정된 ID의 사용자를 찾을 수 없는 경우
     * @throws IllegalStateException 방 생성자가 아닌 사용자가 삭제를 시도하는 경우
     */
    @Transactional
    public void deleteRoom(Long roomId, Long userId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!room.getHost().equals(user)) {
            throw new IllegalStateException("Only the host can delete the room");
        }

        // 방 삭제 전 방에 잇는 모든 유저 삭제
        room.getPlayers().clear();
        room.setPlayersCount(0);

        roomRepository.delete(room);
    }

    /**
     * 게임을 시작하고 WebSocket을 통해 알림을 전송합니다.
     *
     * @param roomId 게임을 시작할 방의 ID
     * @return 업데이트된 Room의 DTO
     * @throws RoomNotFoundException 방을 찾을 수 없을 때 발생
     * @throws IllegalStateException 방이 가득 차지 않았을 때 발생
     */
    @Transactional
    public RoomDTO startGame(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));

        if (room.getPlayersCount() != room.getMaxPlayers()) {
            throw new IllegalStateException("Cannot start game: room is not full");
        }

        room.setGameStarted(true);
        room.setIsHostWhitePlayer();

        Room updatedRoom = roomRepository.save(room);

        // WebSocket을 통해 게임 시작 알림 전송
        webSocketService.notifyGameStarted(roomId);

        return updatedRoom.toDTO();
    }
}
