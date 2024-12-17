import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";

let stompClient = null;

export const connectWebSocket = (onMessageCallback) => {
    const socket = new SockJS("http://localhost:8080/ws");
    stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
        console.log("Connected to WebSocket");

        // 서버에서 오는 메시지를 처리
        stompClient.subscribe("/topic/game/sessionId", (message) => {
            const response = JSON.parse(message.body);
            onMessageCallback(response);
        });
    }, (error) => {
        console.error("WebSocket connection error:", error);
    });
};

export const sendMove = (moveDTO) => {
    if (stompClient && stompClient.connected) {
        stompClient.send("/app/chess.move", {}, JSON.stringify(moveDTO));
    } else {
        console.error("WebSocket is not connected.");
    }
};
