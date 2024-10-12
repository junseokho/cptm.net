package com.example.chessdotnet.controller;

import com.example.chessdotnet.dto.CreateRoomRequest;
import com.example.chessdotnet.dto.JoinRoomRequest;
import com.example.chessdotnet.dto.RoomDTO;
import com.example.chessdotnet.exception.RoomNotFoundException;
import com.example.chessdotnet.exception.UserNotFoundException;
import com.example.chessdotnet.service.RoomService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * RoomController 클래스에 대한 단위 테스트입니다.
 * 이 테스트 클래스는 방 생성, 참여, 조회 등의 API 엔드포인트를 테스트합니다.
 *
 * @author 전종영
 */
@WebMvcTest(RoomController.class)
public class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;

    @Autowired
    private ObjectMapper objectMapper;

    private RoomDTO testRoom;

    @BeforeEach
    void setUp() {
        testRoom = new RoomDTO();
        testRoom.setId(1L);
        testRoom.setTitle("Test Room");
        testRoom.setHostId(1L);
        testRoom.setHostUsername("testUser");
        testRoom.setPlayersCount(1);
        testRoom.setMaxPlayers(2);
    }

    /**
     * createRoom 엔드포인트가 정상적으로 방을 생성하는지 테스트합니다.
     *
     * @throws Exception 테스트 실행 중 예외 발생 시
     */
    @Test
    @WithMockUser
    void createRoom_ShouldCreateRoom() throws Exception {
        CreateRoomRequest request = new CreateRoomRequest();
        request.setTitle("Test Room");
        request.setHostId(1L);

        when(roomService.createRoom(any(), anyLong())).thenReturn(testRoom);

        mockMvc.perform(post("/api/rooms/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testRoom.getId()))
                .andExpect(jsonPath("$.title").value(testRoom.getTitle()))
                .andExpect(jsonPath("$.hostId").value(testRoom.getHostId()))
                .andExpect(jsonPath("$.hostUsername").value(testRoom.getHostUsername()))
                .andExpect(jsonPath("$.currentPlayers").value(testRoom.getPlayersCount()))
                .andExpect(jsonPath("$.maxPlayers").value(testRoom.getMaxPlayers()));
    }

    /**
     * createRoom 엔드포인트에 대한 입력 유효성 검사를 테스트합니다.
     *
     * @throws Exception 테스트 실행 중 예외 발생 시
     */
    @Test
    @WithMockUser
    void createRoom_ShouldValidateInput() throws Exception {
        CreateRoomRequest request = new CreateRoomRequest();
        request.setTitle("Te");
        request.setHostId(null);

        mockMvc.perform(post("/api/rooms/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid input: 방 제목은 3자에서 50자 사이여야 합니다"));
    }

    /**
     * joinRoom 엔드포인트가 정상적으로 사용자를 방에 참여시키는지 테스트합니다.
     *
     * @throws Exception 테스트 실행 중 예외 발생 시
     */
    @Test
    @WithMockUser
    void joinRoom_ShouldJoinRoom() throws Exception {
        JoinRoomRequest request = new JoinRoomRequest();
        request.setUserId(2L);

        testRoom.setPlayersCount(2);

        when(roomService.joinRoom(anyLong(), anyLong())).thenReturn(testRoom);

        mockMvc.perform(post("/api/rooms/1/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testRoom.getId()))
                .andExpect(jsonPath("$.title").value(testRoom.getTitle()))
                .andExpect(jsonPath("$.currentPlayers").value(testRoom.getPlayersCount()))
                .andExpect(jsonPath("$.maxPlayers").value(testRoom.getMaxPlayers()));
    }

    /**
     * joinRoom 엔드포인트에 대한 입력 유효성 검사를 테스트합니다.
     *
     * @throws Exception 테스트 실행 중 예외 발생 시
     */
    @Test
    @WithMockUser
    void joinRoom_ShouldValidateInput() throws Exception {
        JoinRoomRequest request = new JoinRoomRequest();
        request.setUserId(null);

        mockMvc.perform(post("/api/rooms/1/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid input: 사용자 ID는 필수입니다"));
    }

    /**
     * getAvailableRooms 엔드포인트가 사용 가능한 방 목록을 반환하는지 테스트합니다.
     *
     * @throws Exception 테스트 실행 중 예외 발생 시
     */
    @Test
    @WithMockUser
    void getAvailableRooms_ShouldReturnRooms() throws Exception {
        RoomDTO room2 = new RoomDTO();
        room2.setId(2L);
        room2.setTitle("Room 2");
        room2.setHostId(2L);
        room2.setHostUsername("testUser2");
        room2.setPlayersCount(1);
        room2.setMaxPlayers(2);

        when(roomService.getAvailableRooms()).thenReturn(Arrays.asList(testRoom, room2));

        mockMvc.perform(get("/api/rooms/available")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(testRoom.getId()))
                .andExpect(jsonPath("$[0].title").value(testRoom.getTitle()))
                .andExpect(jsonPath("$[0].hostUsername").value(testRoom.getHostUsername()))
                .andExpect(jsonPath("$[1].id").value(room2.getId()))
                .andExpect(jsonPath("$[1].title").value(room2.getTitle()))
                .andExpect(jsonPath("$[1].hostUsername").value(room2.getHostUsername()));
    }

    /**
     * 존재하지 않는 방에 대한 요청 시 적절한 예외 처리를 하는지 테스트합니다.
     *
     * @throws Exception 테스트 실행 중 예외 발생 시
     */
    @Test
    @WithMockUser
    void joinRoom_ShouldHandleRoomNotFoundException() throws Exception {
        when(roomService.joinRoom(anyLong(), anyLong())).thenThrow(new RoomNotFoundException("Room not found"));

        mockMvc.perform(post("/api/rooms/999/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": 1}"))
                .andExpect(status().isNotFound());
    }

    /**
     * 존재하지 않는 사용자로 방 생성 시 적절한 예외 처리를 하는지 테스트합니다.
     *
     * @throws Exception 테스트 실행 중 예외 발생 시
     */
    @Test
    @WithMockUser
    void createRoom_ShouldHandleUserNotFoundException() throws Exception {
        when(roomService.createRoom(any(), anyLong())).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(post("/api/rooms/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Test Room\", \"hostId\": 999}"))
                .andExpect(status().isNotFound());
    }

    /**
     * 일반적인 예외 발생 시 적절한 처리를 하는지 테스트합니다.
     *
     * @throws Exception 테스트 실행 중 예외 발생 시
     */
    @Test
    @WithMockUser
    void shouldHandleGeneralException() throws Exception {
        when(roomService.createRoom(any(), anyLong())).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/api/rooms/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Test Room\", \"hostId\": 1}"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("예기치 않은 오류가 발생했습니다"));
    }
}
