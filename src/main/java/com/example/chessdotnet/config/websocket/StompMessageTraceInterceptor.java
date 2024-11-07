package com.example.chessdotnet.config.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import java.util.Optional;

/**
 * STOMP 프로토콜 메시지를 가로채어 로깅하는 인터셉터 클래스입니다.
 * 모든 STOMP 명령어(CONNECT, SUBSCRIBE, SEND 등)에 대한 처리와 로깅을 담당합니다.
 *
 * @author 전종영
 * @version 1.0
 * @since 2024-11-01
 */
@Component
@Slf4j
public class StompMessageTraceInterceptor implements ChannelInterceptor {

    /**
     * 메시지가 전송되기 전에 호출되는 메서드입니다.
     * STOMP 메시지의 타입에 따라 적절한 로깅을 수행합니다.
     *
     * @param message 전송될 메시지
     * @param channel 메시지가 전송될 채널
     * @return 처리된 메시지 (수정되지 않은 원본 메시지 반환)
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
                message, StompHeaderAccessor.class);

        if (accessor != null && accessor.getCommand() != null) {
            switch (accessor.getCommand()) {
                case CONNECT:
                    logConnect(accessor);
                    break;
                case SUBSCRIBE:
                    logSubscribe(accessor);
                    break;
                case SEND:
                    logSend(accessor, message);
                    break;
                case DISCONNECT:
                    logDisconnect(accessor);
                    break;
                default:
                    log.debug("Other STOMP command received: {}", accessor.getCommand());
            }
        }

        return message;
    }

    /**
     * CONNECT 명령어 수신 시 로깅을 수행하는 메서드입니다.
     *
     * @param accessor STOMP 헤더 접근자
     */
    private void logConnect(StompHeaderAccessor accessor) {
        log.info("STOMP CONNECT received - Session ID: {} - User: {}",
                accessor.getSessionId(),
                Optional.ofNullable(accessor.getUser())
                        .map(Object::toString)
                        .orElse("Anonymous"));
    }

    /**
     * SUBSCRIBE 명령어 수신 시 로깅을 수행하는 메서드입니다.
     *
     * @param accessor STOMP 헤더 접근자
     */
    private void logSubscribe(StompHeaderAccessor accessor) {
        log.info("STOMP SUBSCRIBE received - Session ID: {} - Destination: {}",
                accessor.getSessionId(),
                accessor.getDestination());
    }

    /**
     * SEND 명령어 수신 시 로깅을 수행하는 메서드입니다.
     *
     * @param accessor STOMP 헤더 접근자
     * @param message 전송된 메시지
     */
    private void logSend(StompHeaderAccessor accessor, Message<?> message) {
        log.info("STOMP SEND received - Session ID: {} - Destination: {} - Payload: {}",
                accessor.getSessionId(),
                accessor.getDestination(),
                message.getPayload());
    }

    /**
     * DISCONNECT 명령어 수신 시 로깅을 수행하는 메서드입니다.
     *
     * @param accessor STOMP 헤더 접근자
     */
    private void logDisconnect(StompHeaderAccessor accessor) {
        log.info("STOMP DISCONNECT received - Session ID: {}",
                accessor.getSessionId());
    }
}
