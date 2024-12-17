package com.example.chessdotnet.service.chessGameSession;

import com.example.chessdotnet.entity.ChessGame;
import com.example.chessdotnet.entity.ChessGamePos;
import lombok.Getter;
import lombok.Setter;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;

/**
 * 체스 게임 세션을 관리하는 클래스입니다.
 * 게임의 상태, 플레이어 정보, 타이머, 체스보드를 관리합니다.
 *
 * @author 전종영
 * @version 1.0
 * @since 2024-11-16
 */
@Getter
@Setter
public class ChessGameSession {

    /**
     * 게임에 참여한 사용자들의 ID 배열입니다.
     * index 0: 백색 플레이어, index 1: 흑색 플레이어
     */
    private long[] userIds;

    /**
     * 현재 게임의 고유 식별자입니다.
     */
    private long gameId;

    /**
     * 게임에서 사용되는 체스보드입니다.
     */
    private Chessboard chessboard;

    /**
     * 각 플레이어의 남은 시간을 관리하는 배열입니다.
     * index 0: 백색 플레이어의 시간, index 1: 흑색 플레이어의 시간
     */
    private LeftTime[] leftTime;

    /**
     * 매 수마다 추가되는 시간(초)입니다.
     */
    private int increment;

    /**
     * 게임이 종료되었는지 여부를 나타냅니다.
     */
    private boolean gameEnded;

    /**
     * 타임 컨트롤 설정을 위한 내부 클래스입니다.
     */
    public static class TimeControl {
        public final int initialMinutes;
        public final int initialSeconds;
        public final int incrementSeconds;

        public TimeControl(int minutes, int seconds, int increment) {
            this.initialMinutes = minutes;
            this.initialSeconds = seconds;
            this.incrementSeconds = increment;
        }
    }

    /**
     * 새로운 게임 세션을 생성합니다.
     *
     * @param gameId 게임 식별자
     * @param userIds 참여 사용자 ID 배열
     * @param timeControl 타임 컨트롤 설정
     */
    public ChessGameSession(long gameId, long[] userIds, TimeControl timeControl) {
        this.gameId = gameId;
        this.userIds = Arrays.copyOf(userIds, 2);
        this.chessboard = new Chessboard();
        this.increment = timeControl.incrementSeconds;

        // 타이머 초기화
        this.leftTime = new LeftTime[2];
        int totalSeconds = timeControl.initialMinutes * 60 + timeControl.initialSeconds;
        this.leftTime[0] = new LeftTime(totalSeconds, 0); // 백색
        this.leftTime[1] = new LeftTime(totalSeconds, 0); // 흑색

        this.gameEnded = false;
    }

    /**
     * 기존 게임 기록으로부터 게임 세션을 복원합니다.
     *
     * @param chessGameRecord 기존 게임 기록
     * @param gameId 게임 식별자
     * @param userIds 참여 사용자 ID 배열
     * @param timeControl 타임 컨트롤 설정
     */
    public ChessGameSession(ChessGame chessGameRecord, long gameId, long[] userIds, TimeControl timeControl) {
        this(gameId, userIds, timeControl);

        // 저장된 이동 기록으로 체스보드 상태 복원
        List<ChessGamePos> moveRecords = chessGameRecord.getMoveRecords();

        for (ChessGamePos movePos : moveRecords) {
            // ChessGamePos를 ChessboardMove로 변환
            ChessboardMove move = new ChessboardMove(
                new ChessboardPos(movePos.getStartRow(), movePos.getStartCol()),
                new ChessboardPos(movePos.getEndRow(), movePos.getEndCol())
            );

            // 프로모션 정보 처리
            if (movePos.isPromotion()) {
                move.setPromotionToWhat(Piece.PieceType.valueOf(movePos.getPromotedTo().name()));
            }

            // 체스보드에 이동 적용
            this.chessboard.movePiece(move);
        }

        // 현재 턴 설정
        this.chessboard.turnNow = (moveRecords.size() % 2 == 0) ? Piece.PieceColor.WHITE : Piece.PieceColor.BLACK;

        // 필요한 경우 추가적인 게임 상태 복원
        // 예: 플레이어들의 남은 시간 복원
    }

    /**
     * 현재 턴이 백색 차례인지 확인합니다.
     *
     * @return 백색 차례이면 true
     */
    public boolean isWhiteTurn() {
        return chessboard.turnNow == Piece.PieceColor.WHITE;
    }

    /**
     * 지정된 사용자가 백색 플레이어인지 확인합니다.
     *
     * @param userId 확인할 사용자 ID
     * @return 백색 플레이어이면 true
     */
    public boolean isWhitePlayer(Long userId) {
        return userIds[0] == userId;
    }

    /**
     * 지정된 사용자가 현재 턴의 플레이어인지 확인합니다.
     *
     * @param userId 확인할 사용자 ID
     * @return 현재 턴의 플레이어이면 true
     */
    public boolean isCurrentTurnPlayer(Long userId) {
        return isWhiteTurn() ? isWhitePlayer(userId) : !isWhitePlayer(userId);
    }

    /**
     * 플레이어의 남은 시간을 업데이트합니다.
     *
     * @param playerIndex 플레이어 인덱스 (0: 백색, 1: 흑색)
     * @param spentSeconds 소비한 시간(초)
     * @param spentDeciseconds 소비한 시간(1/10초)
     */
    public void updatePlayerTime(int playerIndex, int spentSeconds, int spentDeciseconds) {
        if (playerIndex >= 0 && playerIndex < 2) {
            leftTime[playerIndex] = leftTime[playerIndex].updateLeftTime(spentSeconds, spentDeciseconds);
        }
    }

    /**
     * 현재 턴 플레이어에게 increment 시간을 추가합니다.
     */
    public void addIncrement() {
        int currentPlayerIndex = isWhiteTurn() ? 0 : 1;
        leftTime[currentPlayerIndex] = leftTime[currentPlayerIndex].updateLeftTime(-increment, 0);
    }

    /**
     * 게임의 고유 식별자를 반환합니다.
     *
     * @return 게임의 ID
     */
    public Long getGameId() {
        return this.gameId;
    }

    /**
     * 게임 관전자들의 세션 ID를 관리하는 Set입니다.
     * ConcurrentHashMap.newKeySet()을 사용하여 동시성을 제어합니다.
     *
     * @author 전종영
     */
    private Set<String> spectatorSessions = ConcurrentHashMap.newKeySet();

    /**
     * 관전자 세션을 추가합니다.
     *
     * @param sessionId 관전자의 WebSocket 세션 ID
     * @author 전종영
     */
    public void addSpectator(String sessionId) {
        spectatorSessions.add(sessionId);
    }

    /**
     * 관전자 세션을 제거합니다.
     *
     * @param sessionId 제거할 관전자의 WebSocket 세션 ID
     * @author 전종영
     */
    public void removeSpectator(String sessionId) {
        spectatorSessions.remove(sessionId);
    }
}