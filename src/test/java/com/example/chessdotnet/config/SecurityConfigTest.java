package com.example.chessdotnet.config;

import com.example.chessdotnet.dto.CreateRoomRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * SecurityConfig 클래스에 대한 통합 테스트입니다.
 *
 * @author 전종영
 */
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 인증되지 않은 사용자가 보호된 엔드포인트에 접근할 수 없는지 테스트합니다.
     *
     * @throws Exception 테스트 실행 중 예외 발생 시
     */
    @Test
    public void givenUnauthenticatedUser_whenAccessingProtectedEndpoint_thenShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/api/rooms/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateRoomRequest())))
                .andExpect(status().isUnauthorized());
    }

    /**
     * 인증된 사용자가 보호된 엔드포인트에 접근할 수 있는지 테스트합니다.
     *
     * @throws Exception 테스트 실행 중 예외 발생 시
     */
    @Test
    @WithMockUser
    public void givenAuthenticatedUser_whenAccessingProtectedEndpoint_thenShouldBeAllowed() throws Exception {
        CreateRoomRequest request = new CreateRoomRequest();
        request.setTitle("Test Room");
        request.setHostId(1L);

        mockMvc.perform(post("/api/rooms/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    /**
     * 모든 사용자가 '/api/rooms/available' 엔드포인트에 접근할 수 있는지 테스트합니다.
     *
     * @throws Exception 테스트 실행 중 예외 발생 시
     */
    @Test
    public void givenAnyUser_whenAccessingAvailableRoomsEndpoint_thenShouldBeAllowed() throws Exception {
        mockMvc.perform(get("/api/rooms/available"))
                .andExpect(status().isOk());
    }

    /**
     * CSRF 보호가 비활성화되어 있는지 테스트합니다.
     *
     * @throws Exception 테스트 실행 중 예외 발생 시
     */
    @Test
    @WithMockUser
    public void givenAuthenticatedUser_whenPostingWithoutCsrfToken_thenShouldBeAllowed() throws Exception {
        CreateRoomRequest request = new CreateRoomRequest();
        request.setTitle("Test Room");
        request.setHostId(1L);

        mockMvc.perform(post("/api/rooms/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}