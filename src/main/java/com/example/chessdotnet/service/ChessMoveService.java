package com.example.chessdotnet.service;

import com.example.chessdotnet.dto.ChessMoveCommand;
import com.example.chessdotnet.dto.ChessMoveResult;
import com.example.chessdotnet.dto.Position;
import com.example.chessdotnet.exception.IllegalMoveException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 체스 게임의 이동 로직을 처리하는 서비스입니다.
 *
 * @author Assistant
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChessMoveService {

    private final RoomService roomService;

    /**
     * 체스 기물의 이동을 실행합니다.
     *
     * @param roomId 게임이 진행중인 방 ID
     * @param moveCommand 이동 명령 정보
     * @return 이동 결과 및 게임 상태
     * @throws IllegalMoveException 잘못된 이동일 경우
     */
    public ChessMoveResult executeMove(Long roomId, ChessMoveCommand moveCommand) {
        log.info("Executing chess move in room {}", roomId);

        // 1. 이동의 기본적인 유효성 검증
        validateMove(moveCommand);

        // 2. 체스 규칙에 따른 이동 가능 여부 확인
        if (!isLegalMove(moveCommand)) {
            throw new IllegalMoveException("This move is not allowed by chess rules");
        }

        // 3. 이동 실행 및 결과 생성
        ChessMoveResult result = new ChessMoveResult();
        try {
            // TODO: 실제 체스 엔진과 연동하여 이동 처리
            result.setSuccess(true);
            result.setBoardState(updateBoardState(moveCommand));

            // 체크/체크메이트 상태 확인
            boolean isCheck = checkForCheck();
            result.setCheck(isCheck);

            if (isCheck) {
                boolean isCheckmate = checkForCheckmate();
                result.setCheckmate(isCheckmate);
            }

        } catch (Exception e) {
            log.error("Failed to execute chess move", e);
            result.setSuccess(false);
            result.setErrorMessage("Failed to execute move: " + e.getMessage());
        }

        return result;
    }

    /**
     * 이동의 기본적인 유효성을 검사합니다.
     *
     * @param moveCommand 검사할 이동 명령
     * @throws IllegalMoveException 유효하지 않은 이동일 경우
     */
    private void validateMove(ChessMoveCommand moveCommand) {
        if (!isValidPosition(moveCommand.getStartPosition()) ||
                !isValidPosition(moveCommand.getEndPosition())) {
            throw new IllegalMoveException("Invalid position coordinates");
        }

        if (moveCommand.getStartPosition().equals(moveCommand.getEndPosition())) {
            throw new IllegalMoveException("Start and end positions cannot be the same");
        }
    }

    /**
     * 주어진 위치가 체스판 범위 내에 있는지 확인합니다.
     *
     * @param position 확인할 위치
     * @return 유효한 위치인지 여부
     */
    private boolean isValidPosition(Position position) {
        return position != null &&
                position.getX() >= 0 && position.getX() < 8 &&
                position.getY() >= 0 && position.getY() < 8;
    }

    /**
     * 체스 규칙에 따라 이동이 가능한지 확인합니다.
     *
     * @param moveCommand 확인할 이동 명령
     * @return 이동 가능 여부
     */
    private boolean isLegalMove(ChessMoveCommand moveCommand) {
        // TODO: 실제 체스 규칙에 따른 이동 가능 여부 확인 로직 구현
        return true;
    }

    /**
     * 현재 체스판 상태를 업데이트합니다.
     *
     * @param moveCommand 실행된 이동 명령
     * @return 업데이트된 체스판 상태
     */
    private String updateBoardState(ChessMoveCommand moveCommand) {
        // TODO: 실제 체스판 상태 업데이트 로직 구현
        return "current board state";
    }

    /**
     * 체크 상태인지 확인합니다.
     *
     * @return 체크 상태 여부
     */
    private boolean checkForCheck() {
        // TODO: 체크 상태 확인 로직 구현
        return false;
    }

    /**
     * 체크메이트 상태인지 확인합니다.
     *
     * @return 체크메이트 상태 여부
     */
    private boolean checkForCheckmate() {
        // TODO: 체크메이트 상태 확인 로직 구현
        return false;
    }
}