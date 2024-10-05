package com.example.chessdotnet.controller;

import com.example.chessdotnet.dto.CreateRoomRequest;
import com.example.chessdotnet.dto.JoinRoomRequest;
import com.example.chessdotnet.entity.Room;
import com.example.chessdotnet.exception.RoomNotFoundException;
import com.example.chessdotnet.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import com.example.chessdotnet.service.RoomService;

import java.util.List;

/**
 * 방 관련 HTTP 요청을 처리하는 컨트롤러 클래스입니다.
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
    public ResponseEntity<Room> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        log.info("방 생성 요청: {}", request.getTitle());
        Room room = roomService.createRoom(request.getTitle(), request.getCreatorId());
        log.info("방 생성 완료. 방 ID: {}", room.getId());
        return ResponseEntity.ok(room);
    }

    /**
     * 사용자가 특정 방에 참여합니다.
     *
     * @param roomId 참여할 방의 ID
     * @param request 방 참여 요청 정보
     * @return 업데이트된 방 정보
     */
    @PostMapping("/{roomId}/join")
    public ResponseEntity<Room> joinRoom(@PathVariable Long roomId, @Valid @RequestBody JoinRoomRequest request) {
        log.info("방 참여 요청. 방 ID: {}, 사용자 ID: {}", roomId, request.getUserId());
        Room room = roomService.joinRoom(roomId, request.getUserId());
        log.info("방 참여 완료. 방 ID: {}, 사용자 ID: {}", roomId, request.getUserId());
        return ResponseEntity.ok(room);
    }

    /**
     * 사용 가능한 모든 방의 목록을 조회합니다.
     *
     * @return 사용 가능한 방 목록
     */
    @GetMapping("/available")
    public ResponseEntity<List<Room>> getAvailableRooms() {
        log.info("사용 가능한 방 목록 요청");
        List<Room> rooms = roomService.getAvailableRooms();
        log.info("사용 가능한 방 {} 개 조회됨", rooms.size());
        return ResponseEntity.ok(rooms);
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
        return ResponseEntity.badRequest().body("Invalid input: " + ex.getBindingResult().getAllErrors().getFirst().getDefaultMessage());
    }


}