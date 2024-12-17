package com.example.chessdotnet.service.chessGameSession;

import com.example.chessdotnet.dto.ChessMoveDTO;
import com.example.chessdotnet.dto.game.*;
import com.example.chessdotnet.entity.ChessGame;
import com.example.chessdotnet.entity.ChessGamePos;
import com.example.chessdotnet.entity.Room;
import com.example.chessdotnet.exception.*;
import com.example.chessdotnet.repository.ChessGameRepository;
import com.example.chessdotnet.repository.RoomRepository;
import com.example.chessdotnet.service.WebSocketService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 체스 게임 세션을 관리하고 게임 로직을 처리하는 서비스 클래스입니다.
 * WebSocket 연결을 통한 실시간 게임 진행을 관리하며,
 * 체스 규칙에 따른 이동 검증과 게임 상태 관리를 담당합니다.
 *
 * @author 전종영
 * @version 2.0
 * @since 2024-11-16
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ChessGameService {
    private final ChessGameRepository gameRepository;
    private final RoomRepository roomRepository;
    private final WebSocketService webSocketService;
    private final Map<String, ChessGameSession> gameSessions = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final Map<Long, Long> roomGameMapping = new ConcurrentHashMap<>();  // roomId -> gameId
    private final Map<Long, Set<String>> gameSessionMapping = new ConcurrentHashMap<>();  // gameId -> sessionIds
    private final Map<String, GameSessionInfo> sessionInfoMapping = new ConcurrentHashMap<>();
    private static final Duration RECONNECT_TIMEOUT = Duration.ofMinutes(2);
    private final SimpMessagingTemplate messagingTemplate;  // 생성자 주입 필요

    /**
     * 새로운 체스 게임을 생성하고 초기화합니다.
     *
     * @param roomId 게임을 생성할 방의 ID
     * @return 생성된 ChessGame 엔티티
     * @throws InvalidGameStateException 방을 찾을 수 없거나 게임을 생성할 수 없는 상태인 경우
     */
    @Transactional
    public ChessGame createGame(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new InvalidGameStateException("Room not found"));

        ChessGame game = new ChessGame(room, room.getHostPlayer().getId());
        game = gameRepository.save(game);

        // 새로운 매핑 추가
        roomGameMapping.put(roomId, game.getId());
        gameSessionMapping.put(game.getId(), ConcurrentHashMap.newKeySet());

        // 기존 코드 유지
        ChessGameSession.TimeControl timeControl = new ChessGameSession.TimeControl(
                room.getTimeControlMin(),
                room.getTimeControlSec(),
                room.getTimeControlInc()
        );
        long[] userIds = {room.getHostPlayer().getId(), room.getJoinedPlayer().getId()};
        ChessGameSession session = new ChessGameSession(game.getId(), userIds, timeControl);
        gameSessions.put(roomId.toString(), session);

        startGameTimer(roomId.toString(), session);

        return game;
    }

    /**
     * 체스 기물 이동을 처리합니다.
     * 이동의 유효성을 검사하고, 이동이 성공하면 게임 상태를 업데이트하고 참가자들에게 알립니다.
     * 체크메이트나 스테일메이트가 발생하면 게임 종료를 알립니다.
     *
     * @param sessionId WebSocket 세션 ID
     * @param moveDTO 이동 정보를 담은 DTO
     * @return 이동 성공 여부
     * @throws InvalidGameStateException 존재하지 않는 세션이거나 이미 종료된 게임인 경우
     * @throws InvalidTurnException 잘못된 턴에 이동을 시도한 경우
     * @throws IllegalMoveException 체스 규칙에 어긋나는 이동을 시도한 경우
     * @author 전종영
     */
    public boolean doMove(String sessionId, ChessMoveDTO moveDTO) {
        try {
            ChessGameSession session = getValidSession(sessionId);
            validateTurn(session, moveDTO);
            validateGameState(session);

            ChessboardMove move = convertToChessboardMove(moveDTO);
            boolean moveResult = session.getChessboard().tryMovePiece(move);

            if (moveResult) {
                // 게임 상태 업데이트 알림
                webSocketService.notifyGameState(sessionId, session);

                // 게임 종료 체크 및 알림
                ChessGame.GameStatus status = checkGameStatus(sessionId);
                if (status == ChessGame.GameStatus.CHECKMATE ||
                        status == ChessGame.GameStatus.STALEMATE) {
                    webSocketService.notifyGameEnd(sessionId, status);
                }
            }

            return moveResult;
        } catch (Exception e) {
            webSocketService.notifyError(sessionId, e.getMessage());
            throw e;
        }
    }

    /**
     * 게임의 현재 상태를 확인하고 반환합니다.
     *
     * @param sessionId WebSocket 세션 ID
     * @return 현재 게임 상태
     */
    public ChessGame.GameStatus checkGameStatus(String sessionId) {
        ChessGameSession session = getValidSession(sessionId);
        Chessboard board = session.getChessboard();
        King king = board.turnNow == Piece.PieceColor.WHITE ? board.whiteKing : board.blackKing;

        if (isCheckmate(session, king)) {
            return ChessGame.GameStatus.CHECKMATE;
        } else if (isStalemate(session, king)) {
            return ChessGame.GameStatus.STALEMATE;
        } else if (king.checked()) {
            return ChessGame.GameStatus.CHECK;
        }

        return ChessGame.GameStatus.IN_PROGRESS;
    }

    /**
     * 기권 처리를 수행합니다.
     *
     * @param sessionId WebSocket 세션 ID
     * @param userId 기권하는 사용자의 ID
     * @return 기권 처리 성공 여부
     */
    @Transactional
    public boolean doResign(String sessionId, Long userId) {
        ChessGameSession session = getValidSession(sessionId);
        if (!isValidPlayer(session, userId)) {
            throw new InvalidGameStateException("User is not a player in this game");
        }

        session.setGameEnded(true);
        ChessGame game = gameRepository.findById(session.getGameId())
                .orElseThrow(() -> new InvalidGameStateException("Game not found"));
        game.endGame();
        gameRepository.save(game);

        log.info("Player {} resigned in session {}", userId, sessionId);
        return true;
    }

    /**
     * 게임 세션을 조회합니다.
     *
     * @param sessionId 세션 ID
     * @return ChessGameSession 인스턴스
     * @throws InvalidGameStateException 세션을 찾을 수 없는 경우
     */
    public ChessGameSession getChessGameSession(String sessionId) {
        ChessGameSession session = gameSessions.get(sessionId);
        if (session == null) {
            throw new InvalidGameStateException("Game session not found");
        }
        return session;
    }

    private void startGameTimer(String sessionId, ChessGameSession session) {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (!session.isGameEnded()) {
                    boolean timeExpired = updateGameTime(session);
                    if (timeExpired) {
                        handleTimeExpired(sessionId, session);
                    }
                }
            } catch (Exception e) {
                log.error("Timer error for session {}: {}", sessionId, e.getMessage());
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    private boolean updateGameTime(ChessGameSession session) {
        int currentPlayerIndex = session.isWhiteTurn() ? 0 : 1;
        LeftTime currentTime = session.getLeftTime()[currentPlayerIndex];

        if (currentTime.leftSeconds <= 0 && currentTime.leftDeciseconds <= 0) {
            return true;
        }

        session.updatePlayerTime(currentPlayerIndex, 0, 1);
        return false;
    }

    private void handleTimeExpired(String sessionId, ChessGameSession session) {
        session.setGameEnded(true);
        try {
            ChessGame game = gameRepository.findById(session.getGameId())
                    .orElseThrow(() -> new InvalidGameStateException("Game not found"));
            game.endGame();
            gameRepository.save(game);
        } catch (Exception e) {
            log.error("Error handling time expiration for session {}: {}", sessionId, e.getMessage());
        }
    }

    private ChessboardMove convertToChessboardMove(ChessMoveDTO moveDTO) {
        ChessboardPos start = new ChessboardPos(
                moveDTO.getStartPosition().getRow(),
                moveDTO.getStartPosition().getCol()
        );
        ChessboardPos end = new ChessboardPos(
                moveDTO.getEndPosition().getRow(),
                moveDTO.getEndPosition().getCol()
        );

        ChessboardMove move = new ChessboardMove(start, end);

        // 특수 이동 처리
        if (moveDTO.getSpecialMoves() != null) {
            if (moveDTO.getSpecialMoves().isPromotion()) {
                move.setPromotionToWhat(Piece.PieceType.valueOf(
                        moveDTO.getSpecialMoves().getPromotionToWhat().toUpperCase()
                ));
            }
            if (moveDTO.getSpecialMoves().isEnpassant()) {
                move.setEnPassant(new ChessboardPos(
                        moveDTO.getSpecialMoves().getTakenPiecePosition().getRow(),
                        moveDTO.getSpecialMoves().getTakenPiecePosition().getCol()
                ));
            }
            if (moveDTO.getSpecialMoves().getCastling() != null) {
                move.setCastling();
            }
        }

        return move;
    }

    private boolean isCheckmate(ChessGameSession session, King king) {
        // 모든 가능한 이동을 시도하여 체크 상태를 벗어날 수 있는지 확인
        return !canEscapeCheck(session.getChessboard(), king.pieceColor);
    }

    private boolean canEscapeCheck(Chessboard board, Piece.PieceColor color) {
        // 해당 색상의 모든 기물에 대해
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(row, col);
                if (!piece.isEmptySquare() && piece.pieceColor == color) {
                    // 각 기물의 가능한 모든 이동을 시도
                    for (ChessboardPos dest : piece.getDestinations()) {
                        ChessboardMove move = new ChessboardMove(piece.position, dest);
                        if (board.tryMovePiece(move)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 유효한 게임 세션을 반환합니다.
     *
     * @param sessionId 검사할 세션 ID
     * @return 유효한 ChessGameSession
     * @throws InvalidGameStateException 세션이 존재하지 않거나 유효하지 않은 경우
     */
    private ChessGameSession getValidSession(String sessionId) {
        ChessGameSession session = gameSessions.get(sessionId);
        if (session == null) {
            throw new InvalidGameStateException("Game session not found");
        }
        if (session.isGameEnded()) {
            throw new GameEndedException("Game has already ended");
        }
        return session;
    }

    /**
     * 현재 턴이 올바른 플레이어의 턴인지 검증합니다.
     *
     * @param session 현재 게임 세션
     * @param moveDTO 이동 요청 정보
     * @throws InvalidTurnException 잘못된 턴에 이동을 시도한 경우
     */
    private void validateTurn(ChessGameSession session, ChessMoveDTO moveDTO) {
        boolean isWhiteTurn = session.isWhiteTurn();
        boolean isWhiteMove = moveDTO.getPiece().getColor().equalsIgnoreCase("white");

        if (isWhiteTurn != isWhiteMove) {
            throw new InvalidTurnException("Not your turn");
        }
    }

    /**
     * 현재 게임 상태가 이동 가능한 상태인지 검증합니다.
     *
     * @param session 현재 게임 세션
     * @throws InvalidGameStateException 게임 상태가 이동 불가능한 상태인 경우
     */
    private void validateGameState(ChessGameSession session) {
        ChessGame.GameStatus status = checkGameStatus(session.getGameId().toString());
        if (status == ChessGame.GameStatus.CHECKMATE ||
                status == ChessGame.GameStatus.STALEMATE) {
            throw new InvalidGameStateException("Game has already ended");
        }

        // 시간 초과 체크
        int playerIndex = session.isWhiteTurn() ? 0 : 1;
        LeftTime currentTime = session.getLeftTime()[playerIndex];
        if (currentTime.leftSeconds <= 0 && currentTime.leftDeciseconds <= 0) {
            throw new InvalidGameStateException("Time has expired");
        }
    }

    /**
     * 체스 게임 이동을 저장합니다.
     *
     * @param session 현재 게임 세션
     * @param moveDTO 이동 요청 정보
     * @param move 실행된 이동
     */
    private void saveGameMove(ChessGameSession session, ChessMoveDTO moveDTO, ChessboardMove move) {
        ChessGame game = gameRepository.findById(session.getGameId())
                .orElseThrow(() -> new InvalidGameStateException("Game not found"));

        ChessGamePos gamePos = new ChessGamePos();
        gamePos.setGame(game);
        gamePos.setMoveNumber(game.getMoveRecords().size() + 1);
        gamePos.setPieceType(ChessGamePos.PieceType.valueOf(moveDTO.getPiece().getName().toUpperCase()));
        gamePos.setPieceColor(moveDTO.getPiece().getColor().equalsIgnoreCase("white") ?
                ChessGamePos.PieceColor.WHITE :
                ChessGamePos.PieceColor.BLACK);
        gamePos.setStartRow(move.startPosition.row);
        gamePos.setStartCol(move.startPosition.col);
        gamePos.setEndRow(move.endPosition.row);
        gamePos.setEndCol(move.endPosition.col);

        // 특수 이동 정보 설정
        if (move.isSpecialMove()) {
            gamePos.setCastling(move.isCastling());
            gamePos.setEnPassant(move.getEnPassantInfo().getFirst());
            if (move.getPromotionInfo().getFirst()) {
                gamePos.setPromotion(true);
                gamePos.setPromotedTo(ChessGamePos.PieceType.valueOf(
                        move.getPromotionInfo().getSecond().name()
                ));
            }
        }

        game.addMoveRecord(gamePos);
        gameRepository.save(game);
    }

    /**
     * 현재 상태가 스테일메이트인지 확인합니다.
     *
     * @param session 게임 세션
     * @param king 현재 턴의 킹
     * @return 스테일메이트 여부
     */
    private boolean isStalemate(ChessGameSession session, King king) {
        if (king.checked()) {
            return false;
        }

        return !canAnyPieceMove(session.getChessboard(), king.pieceColor);
    }

    /**
     * 주어진 색상의 기물들이 어떤 이동이라도 할 수 있는지 확인합니다.
     */
    private boolean canAnyPieceMove(Chessboard board, Piece.PieceColor color) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece piece = board.getPiece(row, col);
                if (!piece.isEmptySquare() && piece.pieceColor == color) {
                    if (!piece.getDestinations().isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 주어진 사용자가 이 게임의 유효한 플레이어인지 확인합니다.
     *
     * @param session 게임 세션
     * @param userId 확인할 사용자 ID
     * @return 유효한 플레이어 여부
     */
    private boolean isValidPlayer(ChessGameSession session, Long userId) {
        return userId != null &&
                (userId.equals(session.getUserIds()[0]) ||
                        userId.equals(session.getUserIds()[1]));
    }

    /**
     * 플레이어의 연결 종료를 처리합니다.
     * 재접속 대기 시간을 설정하고 다른 플레이어들에게 알립니다.
     *
     * @param sessionId 종료된 세션의 ID
     * @return 연결 종료 처리 성공 여부
     * @author 전종영
     */
    public boolean handleDisconnect(String sessionId) {
        GameSessionInfo info = sessionInfoMapping.get(sessionId);
        if (info != null && info.getType() == SessionType.PLAYER) {
            scheduler.schedule(() -> {
                if (!hasReconnected(info.getUserId(), info.getGameId())) {
                    handleGameTimeout(info);
                }
            }, RECONNECT_TIMEOUT.toSeconds(), TimeUnit.SECONDS);

            notifyGameStateChange(new GameStateChangeEvent(
                    GameStateChangeType.PLAYER_DISCONNECTED,
                    info.getGameId(),
                    Map.of("userId", info.getUserId())
            ));
            return true;
        }
        return false;
    }

    /**
     * 게임 상태 변경을 모든 참여자에게 알립니다.
     *
     * @param event 게임 상태 변경 이벤트
     * @author 전종영
     */
    private void notifyGameStateChange(GameStateChangeEvent event) {
        Set<String> sessions = gameSessionMapping.get(event.getGameId());
        if (sessions != null) {
            GameStateMessage message = new GameStateMessage(
                    event.getType(),
                    event.getPayload()
            );

            sessions.forEach(sessionId ->
                    messagingTemplate.convertAndSendToUser(
                            sessionId,
                            "/queue/game.state",
                            message
                    )
            );
        }
    }

    /**
     * 플레이어가 재접속했는지 확인합니다.
     *
     * @param userId 플레이어 ID
     * @param gameId 게임 ID
     * @return 재접속 여부
     * @author 전종영
     */
    private boolean hasReconnected(Long userId, Long gameId) {
        return sessionInfoMapping.values().stream()
                .anyMatch(info -> info.getUserId().equals(userId)
                        && info.getGameId().equals(gameId));
    }

    /**
     * 플레이어의 재접속을 처리합니다.
     *
     * @param userId 플레이어 ID
     * @param newSessionId 새로운 세션 ID
     * @param gameId 게임 ID
     * @return 재접속 처리 성공 여부
     * @throws InvalidGameStateException 유효하지 않은 재접속 시도인 경우
     * @author 전종영
     */
    public boolean handleReconnect(Long userId, String newSessionId, Long gameId) {
        if (isValidReconnection(userId, gameId)) {
            ChessGameSession gameSession = gameSessions.get(String.valueOf(gameId));
            if (gameSession != null && !gameSession.isGameEnded()) {
                updateSessionMappings(userId, newSessionId, gameId);
                notifyGameStateChange(new GameStateChangeEvent(
                        GameStateChangeType.PLAYER_RECONNECTED,
                        gameId,
                        Map.of("userId", userId)
                ));
                return true;
            }
        }
        return false;
    }

    /**
     * 게임 타임아웃을 처리합니다.
     * 플레이어가 재접속 시간 내에 돌아오지 않은 경우 호출됩니다.
     *
     * @param sessionInfo 타임아웃된 세션 정보
     * @author 전종영
     */
    private void handleGameTimeout(GameSessionInfo sessionInfo) {
        ChessGameSession session = gameSessions.get(sessionInfo.getGameId().toString());
        if (session != null && !session.isGameEnded()) {
            // 게임 종료 처리
            session.setGameEnded(true);

            // DB에 게임 종료 상태 저장
            ChessGame game = gameRepository.findById(session.getGameId())
                    .orElseThrow(() -> new InvalidGameStateException("Game not found"));
            game.endGame();
            gameRepository.save(game);

            // 다른 플레이어들에게 알림
            notifyGameStateChange(new GameStateEvent(
                    GameStateChangeType.GAME_ENDED,
                    session.getGameId(),
                    Map.of(
                            "reason", GameEndReason.DISCONNECT_TIMEOUT,
                            "userId", sessionInfo.getUserId()
                    )
            ));
        }
    }

    /**
     * 재접속시 게임 상태를 복구합니다.
     *
     * @param userId 재접속한 사용자 ID
     * @param sessionId 새로운 세션 ID
     * @param gameId 게임 ID
     * @return 상태 복구 성공 여부
     * @author 전종영
     */
    public boolean restoreGameState(Long userId, String sessionId, Long gameId) {
        ChessGameSession session = gameSessions.get(gameId.toString());
        if (session != null && !session.isGameEnded()) {
            // 세션 매핑 업데이트
            updateSessionMappings(userId, sessionId, gameId);

            // 현재 게임 상태 전송
            webSocketService.notifyGameState(sessionId, session);

            // 다른 플레이어들에게 재접속 알림
            notifyGameStateChange(new GameStateEvent(
                    GameStateChangeType.PLAYER_RECONNECTED,
                    gameId,
                    Map.of("userId", userId)
            ));

            return true;
        }
        return false;
    }

    /**
     * 재접속 시도가 유효한지 확인합니다.
     *
     * @param userId 플레이어 ID
     * @param gameId 게임 ID
     * @return 유효한 재접속 시도 여부
     * @author 전종영
     */
    private boolean isValidReconnection(Long userId, Long gameId) {
        ChessGameSession session = gameSessions.get(String.valueOf(gameId));
        return session != null && isValidPlayer(session, userId);
    }

    /**
     * 세션 매핑 정보를 업데이트합니다.
     *
     * @param userId 플레이어 ID
     * @param sessionId 세션 ID
     * @param gameId 게임 ID
     * @author 전종영
     */
    private void updateSessionMappings(Long userId, String sessionId, Long gameId) {
        GameSessionInfo info = new GameSessionInfo(
                userId,
                gameId,
                getRoomIdForGame(gameId),
                SessionType.PLAYER
        );
        sessionInfoMapping.put(sessionId, info);
        gameSessionMapping.computeIfAbsent(gameId, k -> ConcurrentHashMap.newKeySet())
                .add(sessionId);
    }

    /**
     * 게임 ID에 해당하는 방 ID를 조회합니다.
     *
     * @param gameId 게임 ID
     * @return 방 ID
     * @throws InvalidGameStateException 해당하는 방을 찾을 수 없는 경우
     * @author 전종영
     */
    private Long getRoomIdForGame(Long gameId) {
        return roomGameMapping.entrySet().stream()
                .filter(entry -> entry.getValue().equals(gameId))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new InvalidGameStateException("Room not found for game"));
    }

    /**
     * 게임 세션의 상세 정보를 담는 내부 클래스입니다.
     * 플레이어 ID, 게임 ID, 방 ID 및 세션 유형을 포함합니다.
     *
     * @author 전종영
     * @since 2024-11-16
     */
    @Getter
    @AllArgsConstructor
    private static class GameSessionInfo {
        private Long userId;    // 사용자 ID
        private Long gameId;    // 게임 ID
        private Long roomId;    // 방 ID
        private SessionType type; // 세션 유형
    }

    /**
     * 게임 세션의 유형을 정의하는 열거형입니다.
     * 플레이어와 관전자를 구분합니다.
     *
     * @author 전종영
     * @since 2024-11-16
     */
    private enum SessionType {
        PLAYER,    // 플레이어 세션
        SPECTATOR  // 관전자 세션
    }




}