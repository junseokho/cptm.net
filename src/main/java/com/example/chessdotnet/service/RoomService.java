package com.example.chessdotnet.service;

import com.example.chessdotnet.dto.RoomDTO;
import com.example.chessdotnet.dto.RoomStatusMessage;
import com.example.chessdotnet.entity.Room;
import com.example.chessdotnet.entity.User;
import com.example.chessdotnet.entity.ChessGame;
import com.example.chessdotnet.exception.RoomNotFoundException;
import com.example.chessdotnet.exception.UserNotFoundException;
import com.example.chessdotnet.exception.UserNotInRoomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.example.chessdotnet.repository.RoomRepository;
import com.example.chessdotnet.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 체스 게임 방 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 *
 * @author 전종영
 * @version 1.2
 * @since 2024-11-07
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomWebSocketService webSocketService;
    private final ChessGameService chessGameService;

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

        if (room.getPlayersCount() >= room.getMaxPlayers()) {
            throw new RuntimeException("Room is full");
        }

        room.getPlayers().add(user);
        room.setPlayersCount(room.getPlayersCount() + 1);

        if (room.getPlayersCount() == room.getMaxPlayers()) {
            room.setGameStarted(true);
        }

        Room updatedRoom = roomRepository.save(room);
        RoomDTO roomDTO = updatedRoom.toDTO();

        webSocketService.notifyRoomStatusChanged(
                roomDTO,
                room.isGameStarted() ?
                        RoomStatusMessage.MessageType.ROOM_READY :
                        RoomStatusMessage.MessageType.PLAYER_JOINED
        );

        return roomDTO;
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
     * 게임을 시작하고 WebSocket을 통해 알림을 전송합니다.
     *
     * @param roomId 게임을 시작할 방의 ID
     * @param initialPieces 초기 기물 배치 정보
     * @return 업데이트된 Room의 DTO
     * @throws RoomNotFoundException 방을 찾을 수 없을 때 발생
     * @throws IllegalStateException 방이 가득 차지 않았을 때 발생
     */
    @Transactional
    public RoomDTO startGame(Long roomId, List<ChessGameService.InitialPieceDTO> initialPieces) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));

        if (!room.isGameStarted()) {
            throw new IllegalStateException("Cannot start game: room is not ready");
        }

        if (room.getPlayersCount() != room.getMaxPlayers()) {
            throw new IllegalStateException("Cannot start game: room is not full");
        }

        // 체스 게임 생성
        ChessGame game = chessGameService.createGame(roomId, initialPieces);

        // 체스 기물 색상 설정
        room.setIsHostWhitePlayer();

        Room updatedRoom = roomRepository.save(room);
        RoomDTO roomDTO = updatedRoom.toDTO();

        // 게임 시작 알림
        webSocketService.notifyRoomStatusChanged(
                roomDTO,
                RoomStatusMessage.MessageType.GAME_STARTED
        );

        log.info("Game started in room {}, host playing as {}",
                roomId,
                room.getIsHostWhitePlayer() ? "white" : "black");

        return roomDTO;
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

        if (room.isGameStarted() && room.getPlayersCount() <= 1) {
            room.setGameStarted(false);
            webSocketService.notifyRoomStatusChanged(
                    room.toDTO(),
                    RoomStatusMessage.MessageType.GAME_ENDED
            );
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

        room.getPlayers().clear();
        room.setPlayersCount(0);

        roomRepository.delete(room);

        log.info("Room {} deleted by user {}", roomId, userId);
    }
}