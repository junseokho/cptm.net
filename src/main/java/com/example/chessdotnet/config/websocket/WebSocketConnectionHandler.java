package com.example.chessdotnet.config.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * 웹소켓 연결의 생명주기를 추적하고 로깅하는 핸들러 클래스입니다.
 * 웹소켓 연결의 수립, 메시지 수신, 연결 종료 등의 이벤트를 처리합니다.
 *
 * @author 전종영
 * @version 1.0
 * @since 2024-11-01
 */
@Component
@Slf4j
public class WebSocketConnectionHandler extends TextWebSocketHandler {

    /**
     * 웹소켓 연결이 성공적으로 수립되었을 때 호출되는 메서드입니다.
     * 새로운 클라이언트 연결에 대한 정보를 로깅합니다.
     *
     * @param session 새로 생성된 웹소켓 세션
     * @throws Exception 연결 처리 중 발생할 수 있는 예외
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("New WebSocket Connection Established - Session ID: {} - Remote Address: {}",
                session.getId(),
                session.getRemoteAddress());
    }

    /**
     * 클라이언트로부터 텍스트 메시지를 수신했을 때 호출되는 메서드입니다.
     * 수신된 메시지의 내용과 세션 정보를 로깅합니다.
     *
     * @param session 메시지를 전송한 웹소켓 세션
     * @param message 수신된 텍스트 메시지
     * @throws Exception 메시지 처리 중 발생할 수 있는 예외
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        log.info("Received message from Session ID: {} - Message: {}",
                session.getId(),
                message.getPayload());
    }

    /**
     * 웹소켓 연결이 종료될 때 호출되는 메서드입니다.
     * 연결 종료 사유와 세션 정보를 로깅합니다.
     *
     * @param session 종료된 웹소켓 세션
     * @param status 연결 종료 상태 정보
     * @throws Exception 연결 종료 처리 중 발생할 수 있는 예외
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("WebSocket Connection Closed - Session ID: {} - Status: {} - Reason: {}",
                session.getId(),
                status.getCode(),
                status.getReason());
    }
}


