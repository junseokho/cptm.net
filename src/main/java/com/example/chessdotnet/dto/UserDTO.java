package com.example.chessdotnet.dto;

import lombok.Data;

/**
 * 사용자 정보를 전송하기 위한 데이터 전송 객체(DTO)입니다.
 *
 * @author 전종영
 */
@Data
public class UserDTO {
    /** 사용자의 고유 식별자 */
    private Long id;

    /** 사용자의 고유한 이름 */
    private String username;

    /** 사용자가 생성한 방의 수 */
    private int createdRoomsCount;

    /** 사용자가 참여한 방의 수 */
    private int joinedRoomsCount;

    /** 사용자의 레이팅 점수 */
    private int rating;
}