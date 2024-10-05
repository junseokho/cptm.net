package com.example.chessdotnet.controller;

import com.example.chessdotnet.dto.CreateRoomRequest;
import com.example.chessdotnet.dto.JoinRoomRequest;
import com.example.chessdotnet.entity.Room;
import com.example.chessdotnet.exception.RoomNotFoundException;
import com.example.chessdotnet.exception.UserNotFoundException;
import com.example.chessdotnet.service.RoomService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * RoomController 클래스에 대한 단위 테스트입니다.
 */
@WebMvcTest(RoomController.class)
public class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * createRoom 엔드포인트가 정상적으로 방을 생성하는지 테스트합니다.
     */
    @Test
    @WithMockUser
    void createRoom_ShouldCreateRoom() throws Exception {
        CreateRoomRequest request = new CreateRoomRequest();
        request.setTitle("Test Room");
        request.setCreatorId(1L);

        Room createdRoom = new Room();
        createdRoom.setId(1L);
        createdRoom.setTitle("Test Room");

        when(roomService.createRoom(any(), anyLong())).thenReturn(createdRoom);

        mockMvc.perform(post("/api/rooms/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Room"));
    }

    /**
     * joinRoom 엔드포인트가 정상적으로 사용자를 방에 참여시키는지 테스트합니다.
     */
    @Test
    @WithMockUser
    void joinRoom_ShouldJoinRoom() throws Exception {
        JoinRoomRequest request = new JoinRoomRequest();
        request.setUserId(2L);

        Room updatedRoom = new Room();
        updatedRoom.setId(1L);
        updatedRoom.setTitle("Test Room");

        when(roomService.joinRoom(anyLong(), anyLong())).thenReturn(updatedRoom);

        mockMvc.perform(post("/api/rooms/1/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Room"));
    }

    /**
     * getAvailableRooms 엔드포인트가 사용 가능한 방 목록을 반환하는지 테스트합니다.
     */
    @Test
    @WithMockUser
    void getAvailableRooms_ShouldReturnRooms() throws Exception {
        Room room1 = new Room();
        room1.setId(1L);
        room1.setTitle("Room 1");

        Room room2 = new Room();
        room2.setId(2L);
        room2.setTitle("Room 2");

        when(roomService.getAvailableRooms()).thenReturn(Arrays.asList(room1, room2));

        mockMvc.perform(get("/api/rooms/available")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Room 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Room 2"));
    }

    /**
     * 존재하지 않는 방에 대한 요청 시 적절한 예외 처리를 하는지 테스트합니다.
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
     */
    @Test
    @WithMockUser
    void createRoom_ShouldHandleUserNotFoundException() throws Exception {
        when(roomService.createRoom(any(), anyLong())).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(post("/api/rooms/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Test Room\", \"creatorId\": 999}"))
                .andExpect(status().isNotFound());
    }
}