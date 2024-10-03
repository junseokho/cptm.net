package com.example.chessdotnet.controller;

import com.example.chessdotnet.dto.CreateRoomRequest;
import com.example.chessdotnet.dto.JoinRoomRequest;
import com.example.chessdotnet.entity.Room;
import com.example.chessdotnet.exception.RoomNotFoundException;
import com.example.chessdotnet.service.RoomService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomController.class)
class RoomControllerSpringTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private RoomService roomService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    void createRoom_ShouldReturnCreatedRoom() throws Exception {
        CreateRoomRequest request = new CreateRoomRequest();
        request.setTitle("Test Room");
        request.setCreatorId(1L);

        Room createdRoom = new Room();
        createdRoom.setId(1L);
        createdRoom.setTitle("Test Room");

        when(roomService.createRoom(any(), anyLong())).thenReturn(createdRoom);

        mockMvc.perform(post("/api/rooms/create")
                        .with(csrf())
                        .with(user("testUser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Room"));
    }

    @Test
    void joinRoom_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        JoinRoomRequest request = new JoinRoomRequest();
        // userId를 설정하지 않음

        mockMvc.perform(post("/api/rooms/1/join")
                        .with(csrf())
                        .with(user("testUser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAvailableRooms_ShouldReturnListOfRooms() throws Exception {
        Room room1 = new Room();
        room1.setId(1L);
        room1.setTitle("Room 1");

        Room room2 = new Room();
        room2.setId(2L);
        room2.setTitle("Room 2");

        when(roomService.getAvailableRooms()).thenReturn(Arrays.asList(room1, room2));

        mockMvc.perform(get("/api/rooms/available")
                        .with(user("testUser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Room 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Room 2"));
    }

    @Test
    void handleNotFoundExceptions_ShouldReturnNotFound() throws Exception {
        JoinRoomRequest request = new JoinRoomRequest();
        request.setUserId(1L);  // 유효한 userId 설정

        when(roomService.joinRoom(anyLong(), anyLong())).thenThrow(new RoomNotFoundException("Room not found"));

        mockMvc.perform(post("/api/rooms/1/join")
                        .with(csrf())
                        .with(user("testUser").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}