package com.example.chessdotnet.service;

import com.example.chessdotnet.entity.Room;
import com.example.chessdotnet.entity.User;
import com.example.chessdotnet.exception.UserNotFoundException;
import org.springframework.stereotype.Service;
import com.example.chessdotnet.repository.RoomRepository;
import com.example.chessdotnet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 체스 게임 방 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 *
 * @author 전종영
 */
@Service // 비즈니스 로직을 담당하는 서비스 클래스임을 나타냄
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 새로운 방을 생성합니다.
     *
     * @author 전종영
     * @param title 생성할 방의 제목
     * @param creatorId 방을 생성하는 사용자의 ID
     * @return 생성된 Room 객체
     * @throws UserNotFoundException 지정된 ID의 사용자를 찾을 수 없는 경우
     */
    public Room createRoom(String title, Long creatorId) {
        // 생성자 ID로 사용자를 찾음
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + creatorId));

        // 새 Room 객체 생성 및 초기화
        Room room = new Room();
        room.setTitle(title);
        room.setCreator(creator);
        room.getPlayers().add(creator);

        // 방을 저장하고 반환
        return roomRepository.save(room);
    }

    /**
     * 사용자가 특정 게임 방에 참여합니다.
     *
     * @author 전종영
     * @param roomId 참여할 방의 ID
     * @param userId 참여하는 사용자의 ID
     * @return 업데이트된 Room 객체
     * @throws RuntimeException 방을 찾을 수 없거나, 방이 가득 찼거나, 사용자를 찾을 수 없는 경우
     */
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

    /**
     * 게임이 시작되지 않은 모든 방의 목록을 조회합니다.
     *
     * @author 전종영
     * @return 사용 가능한 Room 객체들의 List
     */
    public List<Room> getAvailableRooms() {
        return roomRepository.findByIsGameStartedFalse();
    }
}