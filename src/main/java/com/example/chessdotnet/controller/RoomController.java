package com.example.chessdotnet.controller;

import com.example.chessdotnet.dto.CreateRoomRequest;
import com.example.chessdotnet.dto.JoinRoomRequest;
import com.example.chessdotnet.entity.Room;
import com.example.chessdotnet.exception.RoomNotFoundException;
import com.example.chessdotnet.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import com.example.chessdotnet.service.RoomService;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor // 필수 생성자 자동 생성
@Slf4j // 로깅을 위한 어노테이션
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/create")
    public ResponseEntity<Room> createRoom(@Valid @RequestBody CreateRoomRequest request) {
        // 방 생성 요청 처리
        log.info("방 생성 요청: {}", request.getTitle());
        Room room = roomService.createRoom(request.getTitle(), request.getCreatorId());
        log.info("방 생성 완료. 방 ID: {}", room.getId());
        return ResponseEntity.ok(room);
    }

    @PostMapping("/{roomId}/join")
    public ResponseEntity<Room> joinRoom(@PathVariable Long roomId, @Valid @RequestBody JoinRoomRequest request) {
        // 방 참여 요청 처리
        log.info("방 참여 요청. 방 ID: {}, 사용자 ID: {}", roomId, request.getUserId());
        Room room = roomService.joinRoom(roomId, request.getUserId());
        log.info("방 참여 완료. 방 ID: {}, 사용자 ID: {}", roomId, request.getUserId());
        return ResponseEntity.ok(room);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Room>> getAvailableRooms() {
        // 사용 가능한 방 목록 조회
        log.info("사용 가능한 방 목록 요청");
        List<Room> rooms = roomService.getAvailableRooms();
        log.info("사용 가능한 방 {} 개 조회됨", rooms.size());
        return ResponseEntity.ok(rooms);
    }

    @ExceptionHandler({RoomNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<String> handleNotFoundExceptions(Exception ex) {
        // 방 또는 사용자를 찾을 수 없을 때의 예외 처리
        log.error("Not found error: ", ex);
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralExceptions(Exception ex) {
        // 일반적인 예외 처리
        log.error("Unexpected error: ", ex);
        return ResponseEntity.internalServerError().body("예기치 않은 오류가 발생했습니다");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // 입력 유효성 검사 실패 시 예외 처리
        return ResponseEntity.badRequest().body("Invalid input: " + ex.getBindingResult().getAllErrors().getFirst().getDefaultMessage());
    }
}