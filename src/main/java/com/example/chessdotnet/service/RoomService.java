package com.example.chessdotnet.service;

import com.example.chessdotnet.dto.Room.CreateRoomRequest;
import com.example.chessdotnet.dto.Room.JoinRoomRequest;
import com.example.chessdotnet.dto.Room.RoomDTO;
// import com.example.chessdotnet.dto.RoomStatusMessage;
import com.example.chessdotnet.dto.Room.SpectateRoomRequest;
import com.example.chessdotnet.entity.Room;
import com.example.chessdotnet.entity.User;
import com.example.chessdotnet.entity.ChessGame;
import com.example.chessdotnet.exception.RoomNotFoundException;
import com.example.chessdotnet.exception.UserNotFoundException;
import com.example.chessdotnet.exception.UserNotInRoomException;
import com.example.chessdotnet.repository.ChessGameRepository;
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
 * @version 2.0
 * @since 2024-11-25
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ChessGameRepository chessGameRepository;
    private final RoomWebSocketService webSocketService;
    private final ChessGameService chessGameService;

    /**
     * Room 엔티티를 RoomDTO로 변환합니다.
     *
     * @return 변환된 RoomDTO 객체
     */
    public RoomDTO buildRoomDTOFromEntity(Room room) {
        RoomDTO dto = new RoomDTO();

        Long roomId = room.getId();
        User hostPlayer = room.getHostPlayer();
        User joinedPlayer = room.getJoinedPlayer();

        dto.setId(roomId);
        dto.setHostId(hostPlayer.getId());
        dto.setHostUsername(hostPlayer.getUsername());
        dto.setHostRating(hostPlayer.getRating());
        dto.setJoinedId(joinedPlayer == null ? null : joinedPlayer.getId());
        dto.setJoinedUsername(joinedPlayer == null ? null : joinedPlayer.getUsername());
        dto.setJoinedRating(joinedPlayer == null ? null : joinedPlayer.getRating());
        dto.setCanJoinAsPlayer(joinedPlayer == null);
        dto.setCanJoinAsSpectator(roomRepository.canJoinAsSpectator(room.getId()));
        dto.setIsGameDone(!roomRepository.canJoinAsSpectator(room.getId()));
        dto.setTimeControlMin(room.getTimeControlMin());
        dto.setTimeControlSec(room.getTimeControlSec());
        dto.setTimeControlInc(room.getTimeControlInc());

        return dto;
    }

    /**
     * 새로운 방을 생성합니다.
     * 방 생성자는 자동으로 해당 방의 첫 번째 플레이어가 됩니다.
     *
     * @param createRoomRequest 방 생성 요청 정보 (request body)
     * @return 생성된 Room의 DTO
     * @throws UserNotFoundException 지정된 ID의 사용자를 찾을 수 없는 경우
     */
    public RoomDTO createRoom(CreateRoomRequest createRoomRequest) {
        User hostPlayer = userRepository.findById(createRoomRequest.getHostId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Room room = new Room();
        room.setHostPlayer(hostPlayer);
        room.setTimeControlMin(createRoomRequest.getTimeControlMin());
        room.setTimeControlSec(createRoomRequest.getTimeControlSec());
        room.setTimeControlInc(createRoomRequest.getTimeControlInc());

        Room savedRoom = roomRepository.save(room);
        log.info("Room created - ID: {}, Host: {}", savedRoom.getId(), hostPlayer.getUsername());

        return buildRoomDTOFromEntity(savedRoom);
    }

    /**
     * 사용자가 특정 게임 방에 참여합니다.
     * 이미 다른 플레이어가 있는 경우 참여가 제한됩니다.
     *
     * @param request 방 참여 요청 request body*
     * @return 방 참여 요청 반영 후 Room 정보
     * @throws RoomNotFoundException 유효하지 않은 방
     * @throws UserNotFoundException 유효하지 않은 유저
     * @throws IllegalStateException 참여 불가능한 방
     */
    @Transactional
    public RoomDTO joinRoom(JoinRoomRequest request) {
        log.info("방 참여 요청. 방 ID: {}, 사용자 ID: {}", request.getRoomId(), request.getUserId());

        Room roomToJoin = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));
        User joinedPlayer = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // 이미 방에 다른 플레이어가 존재하는 경우
        if (roomToJoin.getJoinedPlayer() != null) {
            throw new IllegalStateException("Cannot join as player: joinedPlayer already exists");
        }

        if (joinedPlayer.getId().equals(roomToJoin.getHostPlayer().getId())) {
            throw new IllegalStateException("User is already in the same room as host");
        }

        roomToJoin.setJoinedPlayer(joinedPlayer);

        Room updatedRoom = roomRepository.save(roomToJoin);

        return buildRoomDTOFromEntity(updatedRoom);
    }

    /**
     * 사용자의 관전 요청을 처리합니다.
     *
     * @param request 방 관전 요청 request body
     * @return 방 참여 요청 반영 후 Room 정보
     * @throws RoomNotFoundException 유효하지 않은 방
     * @throws IllegalStateException 관전 불가능한 방
     */
    public RoomDTO spectateRoom(SpectateRoomRequest request) {
        log.info("방 관전 요청. 방 ID: {}, 사용자 ID: {}", request.getRoomId(), request.getUserId());

        Room roomToSpectate = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));

        if (!roomRepository.canJoinAsSpectator(request.getRoomId())) {
            throw new IllegalStateException("Cannot join as spectator");
        }

        // @TODO 관전자를 핸들링하기 위한 추가 로직 필요
        // 관전자 입장 알림
//        webSocketService.notifyRoomStatusChanged(
//                buildRoomDTOFromEntity(room),
//                RoomStatusMessage.MessageType.SPECTATOR_JOINED
//        );
//
//        log.info("Spectator joined - Room ID: {}, User: {}", roomId, user.getUsername());
//
//        return buildRoomDTOFromEntity(room);

        return buildRoomDTOFromEntity(roomToSpectate);
    }
    /**
     * 참여 가능한 모든 방의 목록을 반환합니다.
     * joinedPlayer 가 없는 (null) 방이 조회됩니다.
     *
     * @return 참여 가능한 방의 RoomDTO 리스트
     */
    public List<RoomDTO> getPlayableRooms() {
        List<Room> playableRooms = roomRepository.findPlayableRooms();
        log.info("Found {} playable rooms", playableRooms.size());
        return playableRooms.stream()
                .map(this::buildRoomDTOFromEntity)
                .toList();
    }

    /**
     * 관전 가능한 모든 Room 의 List 를 반환합니다.
     * 현재 관전 가능한(spectatable) 방은 게임이 시작되지 않은 방을 포함해,
     * 게임이 종료되지 않은 모든 방입니다.
     *
     * @return 관전 가능한 방들의 DTO 리스트
     */
    public List<RoomDTO> getSpectateableRooms() {
        List<Room> spectateableRooms = roomRepository.findSpectatableRooms();
        log.info("Found {} spectateable rooms", spectateableRooms.size());
        return spectateableRooms.stream()
                .map(this::buildRoomDTOFromEntity)
                .toList();
    }

    /**
     * 게임을 시작하고 WebSocket을 통해 알림을 전송합니다.
     * 게임 시작과 함께 자동으로 관전이 가능하도록 설정됩니다.
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

        if (room.getPlayersCount() != 2) {
            throw new IllegalStateException("Cannot start game: waiting for another player");
        }

        // 게임 시작과 함께 자동으로 관전 가능하도록 설정
        room.setCanJoinAsSpectator(true);

        // 체스 게임 생성
        ChessGame game = chessGameService.createGame(roomId);

        Room updatedRoom = roomRepository.save(room);
        RoomDTO roomDTO = updatedRoom.toDTO();

        // 게임 시작 알림 전송
        webSocketService.notifyRoomStatusChanged(
                roomDTO,
                RoomStatusMessage.MessageType.GAME_STARTED
        );

        log.info("Game started - Room ID: {}, Spectating enabled", roomId);
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

        boolean isHost = user.getId().equals(room.getHost().getId());
        boolean isJoinedPlayer = room.getJoinedPlayer() != null &&
                user.getId().equals(room.getJoinedPlayer().getId());

        if (!isHost && !isJoinedPlayer) {
            throw new UserNotInRoomException("User is not in the room");
        }

        if (isJoinedPlayer) {
            room.setJoinedPlayer(null);
            room.setPlayersCount(1);
        } else if (isHost) {
            if (room.getJoinedPlayer() == null) {
                roomRepository.delete(room);
                log.info("Room deleted - ID: {}", roomId);
                return null;
            } else {
                // 참여자가 있는 경우 참여자를 방장으로 승격
                room.setHost(room.getJoinedPlayer());
                room.setJoinedPlayer(null);
                room.setPlayersCount(1);
            }
        }

        Room updatedRoom = roomRepository.save(room);
        log.info("Player left - Room ID: {}, Player: {}", roomId, user.getUsername());
        return updatedRoom.toDTO();
    }

    /**
     * 방을 삭제하는 기능을 처리합니다.
     * 게임 시작 전에만 삭제가 가능하며, `hostPlayer` 만 삭제할 권한이 있습니다.
     *
     * @param roomId 삭제할 방의 ID
     * @param userId 삭제를 요청한 사용자의 ID
     * @throws RoomNotFoundException 지정된 ID의 방을 찾을 수 없는 경우
     * @throws IllegalStateException 이미 게임이 시작된 경우
     */
    @Transactional
    public void deleteRoom(Long roomId, Long userId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));

        // 예외 체크 (validation)
        if (!room.getHostPlayer().getId().equals(userId)) {
            throw new IllegalStateException("Only the host can delete the room");
        }

        ChessGame gameOfRoom = chessGameRepository.findById(roomId).orElse(null);
        // 게임이 이미 시작된 경우
        if (gameOfRoom != null) {
            throw new IllegalStateException("Game already started");
        }

        roomRepository.delete(room);

        log.info("Room deleted - ID: {} by userId: {}", roomId, userId);
    }
}