package com.example.chessdotnet.service;

import com.example.chessdotnet.entity.Room;
import com.example.chessdotnet.entity.User;
import org.springframework.stereotype.Service;
import com.example.chessdotnet.repository.RoomRepository;
import com.example.chessdotnet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service // 비즈니스 로직을 담당하는 서비스 클래스임을 나타냄
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    // 새로운 방을 생성하는 메서드
    public Room createRoom(String title, Long creatorId) {
        // 생성자 ID로 사용자를 찾음
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 새 Room 객체 생성 및 초기화
        Room room = new Room();
        room.setTitle(title);
        room.setCreator(creator);
        room.getPlayers().add(creator);

        // 방을 저장하고 반환
        return roomRepository.save(room);
    }

    // 사용자가 방에 참여하는 메서드
    public Room joinRoom(Long roomId, Long userId) {
        // 방 ID로 방을 찾음
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        // 방이 가득 찼는지 확인
        if (room.getCurrentPlayers() >= room.getMaxPlayers()) {
            throw new RuntimeException("Room is full");
        }

        // 사용자 ID로 사용자를 찾음
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 사용자를 방에 추가
        room.getPlayers().add(user);
        room.setCurrentPlayers(room.getCurrentPlayers() + 1);

        // 방이 가득 찼다면 게임 시작 상태로 변경
        if (room.getCurrentPlayers() == room.getMaxPlayers()) {
            room.setGameStarted(true);
        }

        // 변경된 방 정보를 저장하고 반환
        return roomRepository.save(room);
    }

    // 사용 가능한 (게임이 시작되지 않은) 방들의 목록을 조회하는 메서드
    public List<Room> getAvailableRooms() {
        return roomRepository.findByIsGameStartedFalse();
    }
}