package com.example.chessdotnet.config;

import com.example.chessdotnet.dto.RoomDTO;
import com.example.chessdotnet.dto.RoomStatusMessage;
import com.example.chessdotnet.dto.UserDTO;
import com.example.chessdotnet.service.RoomService;
import com.example.chessdotnet.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * WebSocket 통신에 대한 통합 테스트를 수행하는 클래스입니다.
 * 실제 WebSocket 연결을 통해 메시지 전송과 수신을 테스트하며,
 * 게임방 생성과 게임 시작에 대한 실시간 알림을 검증합니다.
 *
 * @author 전종영
 * @version 1.1
 * @since 2024-11-04
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketIntegrationTest {

    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;
    private StompSession stompSession;
    private BlockingQueue<RoomStatusMessage> messages;

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserService userService;

    /**
     * 각 테스트 실행 전에 WebSocket 연결을 설정합니다.
     * STOMP 클라이언트를 초기화하고 서버와의 연결을 수립합니다.
     *
     * @throws Exception 연결 수립 중 발생할 수 있는 예외
     */
    @BeforeEach
    void setUp() throws Exception {
        messages = new ArrayBlockingQueue<>(1);

        // WebSocket 클라이언트 설정
        var webSocketClient = new StandardWebSocketClient();
        var webSocketTransport = new WebSocketTransport(webSocketClient);
        var sockJsClient = new SockJsClient(List.of(webSocketTransport));

        stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        // 커넥션 핸들러 설정
        var stompSessionHandler = new StompSessionHandlerAdapter() {
            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                log.error("Transport error: ", exception);
            }

            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                log.info("Connected to WebSocket server");
            }
        };

        // STOMP 연결 설정
        String url = String.format("ws://localhost:%d/ws", port);
        stompSession = stompClient.connectAsync(url, stompSessionHandler)
                .get(5, TimeUnit.SECONDS);

        assertTrue(stompSession.isConnected(), "STOMP 세션이 연결되지 않았습니다.");
    }

    /**
     * 각 테스트에서 사용할 메시지 핸들러를 생성합니다.
     *
     * @return StompFrameHandler 인스턴스
     */
    private StompFrameHandler createStompFrameHandler() {
        return new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return RoomStatusMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                log.info("Received message: {}", payload);
                messages.add((RoomStatusMessage) payload);
            }
        };
    }

    /**
     * 방 생성 시 WebSocket을 통한 알림이 올바르게 전송되는지 테스트합니다.
     * 방이 생성되면 해당 방의 토픽으로 ROOM_READY 타입의 메시지가 전송되어야 합니다.
     *
     * @throws Exception 테스트 실행 중 발생할 수 있는 예외
     */
    @Test
    @Transactional
    void testRoomCreationNotification() throws Exception {
        // Given
        StompSession.Subscription subscription = null;
        try {
            subscription = stompSession.subscribe("/topic/rooms/1", createStompFrameHandler());

            // 구독이 설정될 때까지 대기
            Thread.sleep(1000);

            // When
            UserDTO user = userService.createUser("testUser");
            RoomDTO room = roomService.createRoom("Test Room", user.getId());

            // Then
            RoomStatusMessage message = messages.poll(5, TimeUnit.SECONDS);
            assertNotNull(message, "메시지를 받지 못했습니다.");
            assertEquals(RoomStatusMessage.MessageType.ROOM_READY, message.getType());
            assertEquals(room.getId(), message.getRoomId());
        } finally {
            if (subscription != null) {
                subscription.unsubscribe();
            }
        }
    }

    /**
     * 게임 시작 시 WebSocket을 통한 알림이 올바르게 전송되는지 테스트합니다.
     * 게임이 시작되면 해당 방의 토픽으로 GAME_STARTED 타입의 메시지가 전송되어야 합니다.
     *
     * @throws Exception 테스트 실행 중 발생할 수 있는 예외
     */
    @Test
    @Transactional
    void testGameStartNotification() throws Exception {
        // Given
        UserDTO host = userService.createUser("host");
        UserDTO player = userService.createUser("player");
        RoomDTO room = roomService.createRoom("Test Room", host.getId());

        StompSession.Subscription subscription = null;
        try {
            subscription = stompSession.subscribe(
                    "/topic/rooms/" + room.getId(),
                    createStompFrameHandler()
            );

            // 구독이 설정될 때까지 대기
            Thread.sleep(1000);

            // When
            roomService.joinRoom(room.getId(), player.getId());
            roomService.startGame(room.getId());

            // Then
            RoomStatusMessage message = messages.poll(5, TimeUnit.SECONDS);
            assertNotNull(message, "메시지를 받지 못했습니다");
            assertEquals(RoomStatusMessage.MessageType.GAME_STARTED, message.getType());
            assertEquals(room.getId(), message.getRoomId());
        } finally {
            if (subscription != null) {
                subscription.unsubscribe();
            }
        }
    }

    /**
     * 각 테스트 실행 후 WebSocket 연결을 정리합니다.
     * STOMP 세션을 종료하고 클라이언트를 정지시킵니다.
     */
    @AfterEach
    void tearDown() {
        if (stompSession != null && stompSession.isConnected()) {
            stompSession.disconnect();
        }
        if (stompClient != null) {
            stompClient.stop();
        }
    }
}