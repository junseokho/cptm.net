package com.example.chessdotnet.dto;

import lombok.Data;

/**
 * 체스 이동에 대한 결과를 전달하는 DTO입니다.
 *
 * @author Assistant
 */
@Data
public class ChessMoveResult {
    /** 이동이 성공했는지 여부 */
    private boolean success;

    /** 체스판의 현재 상태 */
    private String boardState;

    /** 체크 상태 여부 */
    private boolean isCheck;

    /** 체크메이트 상태 여부 */
    private boolean isCheckmate;

    /** 에러 메시지 (실패시) */
    private String errorMessage;
}
