package com.example.chessdotnet.controller;

import com.example.chessdotnet.dto.CreateUserRequest;
import com.example.chessdotnet.dto.UserDTO;
import com.example.chessdotnet.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 관련 HTTP 요청을 처리하는 컨트롤러 클래스입니다.
 * 이 클래스는 사용자 생성 및 세션 관리와 관련된 엔드포인트를 제공합니다.
 *
 * @author 전종영
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * 새로운 비회원 사용자를 생성합니다.
     * 이 메서드는 임시로 GET 요청을 통해 비회원 사용자를 생성합니다.
     *
     * @return 생성된 사용자의 DTO
     */
    @GetMapping("/create-guest")
    public ResponseEntity<UserDTO> createGuestUser() {
        log.info("비회원 사용자 생성 요청");
        UserDTO user = userService.createGuestUser();
        log.info("비회원 사용자 생성 완료. 사용자 ID: {}", user.getId());
        return ResponseEntity.ok(user);
    }

    /**
     * 새로운 사용자를 생성합니다.
     * 이 메서드는 POST 요청을 통해 새 사용자를 생성하고, 세션 정보를 포함합니다.
     *
     * @param request 사용자 생성 요청 정보
     * @param session HTTP 세션 객체
     * @return 생성된 사용자의 DTO
     */
    @PostMapping("/create")
    public ResponseEntity<UserDTO> createUser(@RequestBody CreateUserRequest request, HttpSession session) {
        log.info("사용자 생성 요청: {}", request.getUsername());
        UserDTO user = userService.createUser(request.getUsername());
        session.setAttribute("userId", user.getId());
        log.info("사용자 생성 완료. 사용자 ID: {}", user.getId());
        return ResponseEntity.ok(user);
    }

    /**
     * 현재 세션의 사용자 정보를 조회합니다.
     *
     * @param session HTTP 세션 객체
     * @return 현재 세션의 사용자 DTO
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        UserDTO user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }
}

