package com.example.chessdotnet.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * RoomDTO 클래스에 대한 단위 테스트입니다.
 *
 * @author 전종영
 */
public class RoomDTOTest {

    /**
     * RoomDTO 객체의 생성 및 getter/setter 메소드를 테스트합니다.
     *
     * @author 전종영
     */
    @Test
    public void testRoomDTOCreationAndGetterSetter() {
        RoomDTO roomDTO = new RoomDTO();

        roomDTO.setId(1L);
        roomDTO.setTitle("Test Room");
        roomDTO.setCreatorId(2L);
        roomDTO.setCreatorUsername("testUser");
        roomDTO.setCurrentPlayers(1);
        roomDTO.setMaxPlayers(2);
        roomDTO.setGameStarted(false);

        assertEquals(1L, roomDTO.getId(), "ID가 올바르게 설정되어야 합니다.");
        assertEquals("Test Room", roomDTO.getTitle(), "제목이 올바르게 설정되어야 합니다.");
        assertEquals(2L, roomDTO.getCreatorId(), "생성자 ID가 올바르게 설정되어야 합니다.");
        assertEquals("testUser", roomDTO.getCreatorUsername(), "생성자 이름이 올바르게 설정되어야 합니다.");
        assertEquals(1, roomDTO.getCurrentPlayers(), "현재 플레이어 수가 올바르게 설정되어야 합니다.");
        assertEquals(2, roomDTO.getMaxPlayers(), "최대 플레이어 수가 올바르게 설정되어야 합니다.");
        assertFalse(roomDTO.isGameStarted(), "게임 시작 상태가 올바르게 설정되어야 합니다.");
    }

    /**
     * RoomDTO 객체의 equals 메소드를 테스트합니다.
     *
     * @author 전종영
     */
    @Test
    public void testRoomDTOEquals() {
        RoomDTO room1 = new RoomDTO();
        room1.setId(1L);
        room1.setTitle("Test Room");

        RoomDTO room2 = new RoomDTO();
        room2.setId(1L);
        room2.setTitle("Test Room");

        RoomDTO room3 = new RoomDTO();
        room3.setId(2L);
        room3.setTitle("Another Room");

        assertEquals(room1, room2, "동일한 ID와 제목을 가진 RoomDTO 객체는 같아야 합니다.");
        assertNotEquals(room1, room3, "다른 ID나 제목을 가진 RoomDTO 객체는 달라야 합니다.");
    }

    /**
     * RoomDTO 객체의 hashCode 메소드를 테스트합니다.
     *
     * @author 전종영
     */
    @Test
    public void testRoomDTOHashCode() {
        RoomDTO room1 = new RoomDTO();
        room1.setId(1L);
        room1.setTitle("Test Room");

        RoomDTO room2 = new RoomDTO();
        room2.setId(1L);
        room2.setTitle("Test Room");

        assertEquals(room1.hashCode(), room2.hashCode(), "동일한 내용의 RoomDTO 객체는 같은 hashCode를 가져야 합니다.");
    }
}