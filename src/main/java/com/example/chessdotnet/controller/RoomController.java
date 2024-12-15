package com.example.chessdotnet.controller;

import com.example.chessdotnet.dto.Room.CreateRoomRequest;
import com.example.chessdotnet.dto.Room.JoinRoomRequest;
import com.example.chessdotnet.dto.Room.LeaveRoomRequest;
import com.example.chessdotnet.dto.Room.RoomDTO;
import com.example.chessdotnet.exception.RoomNotFoundException;
import com.example.chessdotnet.exception.UserNotFoundException;
import com.example.chessdotnet.exception.UserNotInRoomException;
import com.example.chessdotnet.service.ChessGameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import com.example.chessdotnet.service.RoomService;

import java.util.List;

/**
 * 방 관련 HTTP 요청을 처리하는 컨트롤러 클래스입니다.
 *
 * @author 전종영
 * @version 1.2
 * @since 2024-11-07
 */
@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Slf4j
public class RoomController {

    private final RoomService roomService;

    /**
     * 새로운 방을 생성합니다.
     *
     * @param request 방 생성 요청 정보
     * @return 생성된 방 정보
     */
    @PostMapping("/create")
    public ResponseEntity<RoomDTO> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        RoomDTO createdRoom;
        try {
            createdRoom = roomService.createRoom(request);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
        log.info("방 생성 완료. 방 ID: {}", createdRoom.getId());
        return ResponseEntity.ok(createdRoom);
    }

    /**
     * 사용자가 특정 방에 참여합니다.
     *
     * @param roomId 참여할 방의 ID
     * @param request 방 참여 요청 정보
     * @return 업데이트된 방 정보
     */
    @PostMapping("/{roomId}/join")
    public ResponseEntity<RoomDTO> joinRoom(@PathVariable Long roomId, @Valid @RequestBody JoinRoomRequest request) {
        log.info("방 참여 요청. 방 ID: {}, 사용자 ID: {}", roomId, request.getUserId());
        RoomDTO room = roomService.joinRoom(roomId, request.getUserId());
        log.info("방 참여 완료. 방 ID: {}, 사용자 ID: {}", roomId, request.getUserId());
        return ResponseEntity.ok(room);
    }

    /**
     * 사용 가능한 모든 방의 목록을 조회합니다.
     *
     * @return 사용 가능한 방 목록
     */
    @GetMapping("/playable")
    public ResponseEntity<List<RoomDTO>> getPlayableRooms() {
        List<RoomDTO> rooms = roomService.getPlayableRooms();
        return ResponseEntity.ok(rooms);
    }

    /**
     * 게임을 시작하고 체스 기물을 초기 배치합니다.
     *
     * @param roomId 게임을 시작할 방의 ID
     * @param initialPieces 초기 기물 배치 정보 리스트
     * @return 업데이트된 방 정보
     */
    @PostMapping("/{roomId}/start")
    public ResponseEntity<RoomDTO> startGame(
            @PathVariable Long roomId,
            @Valid @RequestBody List<ChessGameService.InitialPieceDTO> initialPieces) {
        log.info("게임 시작 요청. 방 ID: {}", roomId);
        RoomDTO room = roomService.startGame(roomId, initialPieces);
        log.info("게임 시작 완료. 방 ID: {}, 방장 색상: {}",
                roomId,
                room.getIsHostWhitePlayer() ? "백" : "흑");
        return ResponseEntity.ok(room);
    }

    /**
     * 사용자가 방을 나가는 요청을 처리합니다.
     *
     * @param roomId 나가려는 방의 ID
     * @param request 방을 나가려는 사용자의 정보
     * @return 업데이트된 방 정보 또는 방이 삭제되었을 경우 204 No Content
     */
    @PostMapping("/{roomId}/leave")
    public ResponseEntity<RoomDTO> leaveRoom(@PathVariable Long roomId, @Valid @RequestBody LeaveRoomRequest request) {
        log.info("방 나가기 요청. 방 ID: {}, 사용자 ID: {}", roomId, request.getUserId());
        RoomDTO room = roomService.leaveRoom(roomId, request.getUserId());
        if (room == null) {
            log.info("방 삭제됨. 방 ID: {}", roomId);
            return ResponseEntity.noContent().build();
        }
        log.info("방 나가기 완료. 방 ID: {}, 사용자 ID: {}", roomId, request.getUserId());
        return ResponseEntity.ok(room);
    }

    /**
     * 방을 삭제하는 요청을 처리합니다.
     *
     * @param roomId 삭제할 방의 ID
     * @param userId 삭제를 요청한 사용자의 ID
     * @return 204 No Content
     */
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long roomId, @RequestParam Long userId) {
        log.info("방 삭제 요청. 방 ID: {}, 사용자 ID: {}", roomId, userId);
        try {
            roomService.deleteRoom(roomId, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * UserNotInRoomException 발생 시 처리합니다.
     *
     * @param ex 발생한 예외
     * @return 400 Bad Request 응답
     */
    @ExceptionHandler(UserNotInRoomException.class)
    public ResponseEntity<String> handleUserNotInRoomException(UserNotInRoomException ex) {
        log.error("User not in room error: ", ex);
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    /**
     * IllegalStateException 발생 시 처리합니다.
     *
     * @param ex 발생한 예외
     * @return 400 Bad Request 응답
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
        log.error("Illegal state error: ", ex);
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    /**
     * RoomNotFoundException 또는 UserNotFoundException 발생 시 처리합니다.
     *
     * @param ex 발생한 예외
     * @return 404 Not Found 응답
     */
    @ExceptionHandler({RoomNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<String> handleNotFoundExceptions(Exception ex) {
        log.error("Not found error: ", ex);
        return ResponseEntity.notFound().build();
    }

    /**
     * 일반적인 예외 발생 시 처리합니다.
     *
     * @param ex 발생한 예외
     * @return 500 Internal Server Error 응답
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralExceptions(Exception ex) {
        log.error("Unexpected error: ", ex);
        return ResponseEntity.internalServerError().body("예기치 않은 오류가 발생했습니다");
    }

    /**
     * 입력 유효성 검사 실패 시 처리합니다.
     *
     * @param ex 발생한 예외
     * @return 400 Bad Request 응답
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body("Invalid input: " +
                ex.getBindingResult().getAllErrors().getFirst().getDefaultMessage());
    }
}