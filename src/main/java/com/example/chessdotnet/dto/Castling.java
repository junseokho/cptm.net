package com.example.chessdotnet.dto;

import lombok.Data;

/**
 * 캐슬링 관련 정보를 담는 DTO입니다.
 *
 * @author Assistant
 */
@Data
public class Castling {
    /** 킹사이드 캐슬링 여부 */
    private boolean isKingSide;
}