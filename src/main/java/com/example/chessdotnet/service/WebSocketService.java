package com.example.chessdotnet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * 게임이 시작되었음을 알리는 메시지를 전송합니다.
     * @param roomId 게임이 시작된 방의 ID
     */
    public void notifyGameStarted(Long roomId) {
        messagingTemplate.convertAndSend("/topic/room/" + roomId, "Game Started");
    }
}