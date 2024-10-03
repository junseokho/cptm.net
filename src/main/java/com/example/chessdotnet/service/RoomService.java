package com.example.chessdotnet.service;

import com.example.chessdotnet.entity.Room;
import com.example.chessdotnet.entity.User;
import org.springframework.stereotype.Service;
import com.example.chessdotnet.repository.RoomRepository;
import com.example.chessdotnet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    public Room createRoom(String title, Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Room room = new Room();
        room.setTitle(title);
        room.setCreator(creator);
        room.getPlayers().add(creator);

        return roomRepository.save(room);
    }

    public Room joinRoom(Long roomId, Long userId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (room.getCurrentPlayers() >= room.getMaxPlayers()) {
            throw new RuntimeException("Room is full");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        room.getPlayers().add(user);
        room.setCurrentPlayers(room.getCurrentPlayers() + 1);

        if (room.getCurrentPlayers() == room.getMaxPlayers()) {
            room.setGameStarted(true);
        }

        return roomRepository.save(room);
    }

    public List<Room> getAvailableRooms() {
        return roomRepository.findByIsGameStartedFalse();
    }
}
