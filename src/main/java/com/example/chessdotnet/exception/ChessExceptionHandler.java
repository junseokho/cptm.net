package com.example.chessdotnet.exception;

import com.example.chessdotnet.dto.ChessMoveResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * 체스 게임 관련 예외들을 처리하는 전역 예외 핸들러입니다.
 * WebSocket 메시징에서 발생하는 예외들을 적절히 처리하여 클라이언트에게 전달합니다.
 *
 * @author 전종영
 */
@ControllerAdvice
@Slf4j
public class ChessExceptionHandler {

    /**
     * IllegalMoveException 처리
     *
     * @param ex 발생한 예외
     * @return 에러 정보가 포함된 이동 결과
     */
    @MessageExceptionHandler(IllegalMoveException.class)
    @SendToUser("/queue/errors")
    public ChessMoveResult handleIllegalMove(IllegalMoveException ex) {
        log.error("Illegal move attempted", ex);
        ChessMoveResult result = new ChessMoveResult();
        result.setSuccess(false);
        result.setErrorMessage("Invalid move: " + ex.getMessage());
        return result;
    }

    /**
     * InvalidTurnException 처리
     *
     * @param ex 발생한 예외
     * @return 에러 정보가 포함된 이동 결과
     */
    @MessageExceptionHandler(InvalidTurnException.class)
    @SendToUser("/queue/errors")
    public ChessMoveResult handleInvalidTurn(InvalidTurnException ex) {
        log.error("Move attempted on invalid turn", ex);
        ChessMoveResult result = new ChessMoveResult();
        result.setSuccess(false);
        result.setErrorMessage("Not your turn: " + ex.getMessage());
        return result;
    }

    /**
     * GameEndedException 처리
     *
     * @param ex 발생한 예외
     * @return 에러 정보가 포함된 이동 결과
     */
    @MessageExceptionHandler(GameEndedException.class)
    @SendToUser("/queue/errors")
    public ChessMoveResult handleGameEnded(GameEndedException ex) {
        log.error("Move attempted on ended game", ex);
        ChessMoveResult result = new ChessMoveResult();
        result.setSuccess(false);
        result.setErrorMessage("Game has ended: " + ex.getMessage());
        return result;
    }

    /**
     * InvalidGameStateException 처리
     *
     * @param ex 발생한 예외
     * @return 에러 정보가 포함된 이동 결과
     */
    @MessageExceptionHandler(InvalidGameStateException.class)
    @SendToUser("/queue/errors")
    public ChessMoveResult handleInvalidGameState(InvalidGameStateException ex) {
        log.error("Invalid game state detected", ex);
        ChessMoveResult result = new ChessMoveResult();
        result.setSuccess(false);
        result.setErrorMessage("Invalid game state: " + ex.getMessage());
        return result;
    }

    /**
     * 기타 예외 처리
     *
     * @param ex 발생한 예외
     * @return 에러 정보가 포함된 이동 결과
     */
    @MessageExceptionHandler(Exception.class)
    @SendToUser("/queue/errors")
    public ChessMoveResult handleGeneralException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        ChessMoveResult result = new ChessMoveResult();
        result.setSuccess(false);
        result.setErrorMessage("An unexpected error occurred: " + ex.getMessage());
        return result;
    }
}