package com.example.chessdotnet.service;

import com.example.chessdotnet.dto.ChessMoveDTO;
import com.example.chessdotnet.entity.ChessGame;
import com.example.chessdotnet.entity.Room;
import com.example.chessdotnet.exception.IllegalMoveException;
import com.example.chessdotnet.exception.InvalidGameStateException;
import com.example.chessdotnet.exception.InvalidTurnException;
import com.example.chessdotnet.repository.ChessGameRepository;
import com.example.chessdotnet.repository.RoomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;

/**
 * 체스 게임의 비즈니스 로직을 처리하는 서비스 클래스입니다.
 *
 * @author 전종영
 * @version 1.0
 * @since 2024-11-05
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChessGameService {
    private final ChessGameRepository gameRepository;
    private final RoomRepository roomRepository;
    private final ObjectMapper objectMapper;

    // 체스판의 초기 기물 배치를 정의
    private static final Map<String, String> INITIAL_PIECES = new HashMap<>();
    static {
        // 백색 기물
        INITIAL_PIECES.put("0,0", "wR"); // White Rook
        INITIAL_PIECES.put("1,0", "wN"); // White Knight
        INITIAL_PIECES.put("2,0", "wB"); // White Bishop
        INITIAL_PIECES.put("3,0", "wQ"); // White Queen
        INITIAL_PIECES.put("4,0", "wK"); // White King
        INITIAL_PIECES.put("5,0", "wB"); // White Bishop
        INITIAL_PIECES.put("6,0", "wN"); // White Knight
        INITIAL_PIECES.put("7,0", "wR"); // White Rook
        for (int i = 0; i < 8; i++) {
            INITIAL_PIECES.put(i + ",1", "wP"); // White Pawns
        }

        // 흑색 기물
        INITIAL_PIECES.put("0,7", "bR"); // Black Rook
        INITIAL_PIECES.put("1,7", "bN"); // Black Knight
        INITIAL_PIECES.put("2,7", "bB"); // Black Bishop
        INITIAL_PIECES.put("3,7", "bQ"); // Black Queen
        INITIAL_PIECES.put("4,7", "bK"); // Black King
        INITIAL_PIECES.put("5,7", "bB"); // Black Bishop
        INITIAL_PIECES.put("6,7", "bN"); // Black Knight
        INITIAL_PIECES.put("7,7", "bR"); // Black Rook
        for (int i = 0; i < 8; i++) {
            INITIAL_PIECES.put(i + ",6", "bP"); // Black Pawns
        }
    }
    /**
     * 캐슬링의 유효성을 검증합니다.
     *
     * @param game 현재 게임
     * @param moveDTO 이동 정보
     * @param boardState 현재 보드 상태
     * @throws IllegalMoveException 캐슬링이 불가능한 경우
     */
    private void validateCastling(ChessGame game, ChessMoveDTO moveDTO, ObjectNode boardState) {
        ObjectNode pieces = (ObjectNode) boardState.get("pieces");
        boolean isWhite = game.isWhiteTurn();
        int row = isWhite ? 0 : 7;

        // 킹의 위치 확인
        String kingPos = "4," + row;
        if (!pieces.has(kingPos) || !pieces.get(kingPos).asText().equals(isWhite ? "wK" : "bK")) {
            throw new IllegalMoveException("King must not have moved for castling");
        }

        // 킹사이드 캐슬링
        if (moveDTO.getSpecialMoves().getCastling().isKingSide()) {
            String rookPos = "7," + row;
            if (!pieces.has(rookPos) || !pieces.get(rookPos).asText().equals(isWhite ? "wR" : "bR")) {
                throw new IllegalMoveException("Rook must not have moved for castling");
            }

            // 경로 상의 기물 확인
            for (int x = 5; x < 7; x++) {
                if (pieces.has(x + "," + row)) {
                    throw new IllegalMoveException("Path must be clear for castling");
                }
            }
        }
        // 퀸사이드 캐슬링
        else {
            String rookPos = "0," + row;
            if (!pieces.has(rookPos) || !pieces.get(rookPos).asText().equals(isWhite ? "wR" : "bR")) {
                throw new IllegalMoveException("Rook must not have moved for castling");
            }

            // 경로 상의 기물 확인
            for (int x = 1; x < 4; x++) {
                if (pieces.has(x + "," + row)) {
                    throw new IllegalMoveException("Path must be clear for castling");
                }
            }
        }
    }

    /**
     * 앙파상의 유효성을 검증합니다.
     *
     * @param game 현재 게임
     * @param moveDTO 이동 정보
     * @param boardState 현재 보드 상태
     * @throws IllegalMoveException 앙파상이 불가능한 경우
     */
    private void validateEnPassant(ChessGame game, ChessMoveDTO moveDTO, ObjectNode boardState) {
        ObjectNode pieces = (ObjectNode) boardState.get("pieces");
        int[] start = moveDTO.getStartPosition();
        int[] end = moveDTO.getEndPosition();

        // 앙파상 대상 폰의 위치 확인
        int targetRow = start[1];
        String targetPos = end[0] + "," + targetRow;

        if (!pieces.has(targetPos)) {
            throw new IllegalMoveException("No pawn available for en passant");
        }

        String targetPiece = pieces.get(targetPos).asText();
        if (!targetPiece.endsWith("P")) {
            throw new IllegalMoveException("En passant target must be a pawn");
        }

        // 앙파상은 5번째(흑) 또는 4번째(백) 랭크에서만 가능
        if ((game.isWhiteTurn() && start[1] != 4) || (!game.isWhiteTurn() && start[1] != 3)) {
            throw new IllegalMoveException("Invalid en passant position");
        }
    }

    /**
     * 프로모션의 유효성을 검증합니다.
     *
     * @param moveDTO 이동 정보
     * @throws IllegalMoveException 프로모션이 불가능한 경우
     */
    private void validatePromotion(ChessMoveDTO moveDTO) {
        int[] end = moveDTO.getEndPosition();
        String promotionPiece = moveDTO.getSpecialMoves().getPromotionToWhat();

        // 프로모션은 마지막 랭크에서만 가능
        if (end[1] != 0 && end[1] != 7) {
            throw new IllegalMoveException("Promotion is only possible on the last rank");
        }

        // 승격 기물 유효성 검사
        if (!promotionPiece.matches("[wb][QRBN]")) {
            throw new IllegalMoveException("Invalid promotion piece");
        }
    }

    /**
     * 특수 이동을 처리합니다.
     *
     * @param moveDTO 이동 정보
     * @param pieces 현재 보드의 기물 상태
     * @param currentPiece 현재 이동하는 기물
     */
    private void handleSpecialMoves(ChessMoveDTO moveDTO, ObjectNode pieces, String currentPiece) {
        // 캐슬링 처리
        if (moveDTO.getSpecialMoves().getCastling() != null) {
            handleCastling(moveDTO, pieces, currentPiece);
        }

        // 앙파상 처리
        if (moveDTO.getSpecialMoves().isEnpassant()) {
            String capturedPawnPos = moveDTO.getSpecialMoves().getTakenPiecePosition()[0] + "," +
                    moveDTO.getSpecialMoves().getTakenPiecePosition()[1];
            pieces.remove(capturedPawnPos);
        }

        // 프로모션 처리
        if (moveDTO.getSpecialMoves().isPromotion()) {
            String promotionPiece = moveDTO.getSpecialMoves().getPromotionToWhat();
            String endPos = moveDTO.getEndPosition()[0] + "," + moveDTO.getEndPosition()[1];
            pieces.put(endPos, promotionPiece);
        }
    }

    /**
     * 캐슬링을 처리합니다.
     *
     * @param moveDTO 이동 정보
     * @param pieces 현재 보드의 기물 상태
     * @param currentPiece 현재 이동하는 기물
     */
    private void handleCastling(ChessMoveDTO moveDTO, ObjectNode pieces, String currentPiece) {
        int row = currentPiece.startsWith("w") ? 0 : 7;
        boolean isKingSide = moveDTO.getSpecialMoves().getCastling().isKingSide();

        // 룩 이동
        String oldRookPos = (isKingSide ? "7," : "0,") + row;
        String newRookPos = (isKingSide ? "5," : "3,") + row;
        String rook = pieces.get(oldRookPos).asText();
        pieces.remove(oldRookPos);
        pieces.put(newRookPos, rook);
    }

    /**
     * 주어진 색상의 왕이 체크 상태인지 확인합니다.
     *
     * @param pieces 현재 보드의 기물 상태
     * @param isWhite 확인할 왕의 색상
     * @return 체크 상태 여부
     */
    private boolean checkForCheck(ObjectNode pieces, boolean isWhite) {
        // 왕의 위치 찾기
        String kingPos = findKing(pieces, isWhite);
        if (kingPos == null) {
            return false;
        }

        // 상대방 기물들의 공격 가능 위치 확인
        String[] positions = pieces.fieldNames().next().split(",");
        for (String pos : positions) {
            String piece = pieces.get(pos).asText();
            if (piece.startsWith(isWhite ? "b" : "w")) {
                if (canAttack(pieces, pos, kingPos, piece)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 체크메이트 상태인지 확인합니다.
     *
     * @param pieces 현재 보드의 기물 상태
     * @param isWhite 확인할 왕의 색상
     * @return 체크메이트 상태 여부
     */
    private boolean isCheckmate(ObjectNode pieces, boolean isWhite) {
        // 모든 가능한 이동을 시도하여 체크를 피할 수 있는지 확인
        String[] positions = pieces.fieldNames().next().split(",");
        for (String startPos : positions) {
            String piece = pieces.get(startPos).asText();
            if (piece.startsWith(isWhite ? "w" : "b")) {
                if (hasLegalMoves(pieces, startPos, piece)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 체스 기물을 이동시킵니다.
     *
     * @param roomId 게임이 진행 중인 방의 ID
     * @param moveDTO 이동 정보
     * @param playerId 이동을 시도하는 플레이어의 ID
     * @return 업데이트된 체스 게임
     */
    @Transactional
    public ChessGame doMove(Long roomId, ChessMoveDTO moveDTO, Long playerId) {
        ChessGame game = gameRepository.findByRoom_IdAndStatus(roomId, ChessGame.GameStatus.IN_PROGRESS)
                .orElseThrow(() -> new InvalidGameStateException("No active game found in this room"));

        validateMove(game, moveDTO, playerId);

        // 이동 처리 및 게임 상태 업데이트
        updateGameState(game, moveDTO);
        game.setLastMoveTime(new Date());

        return gameRepository.save(game);
    }


    /**
     * 주어진 위치의 기물이 합법적인 이동이 가능한지 확인합니다.
     *
     * @param pieces 현재 보드의 기물 상태
     * @param startPos 시작 위치
     * @param piece 기물
     * @return 합법적인 이동 가능 여부
     */
    private boolean hasLegalMoves(ObjectNode pieces, String startPos, String piece) {
        // 각 기물 타입에 따른 가능한 이동 확인
        // 실제 구현에서는 각 기물의 이동 규칙에 따라 상세히 구현해야 함
        return false; // 임시 구현
    }

    /**
     * 주어진 색상의 왕의 위치를 찾습니다.
     *
     * @param pieces 현재 보드의 기물 상태
     * @param isWhite 찾을 왕의 색상
     * @return 왕의 위치 ("x,y" 형식) 또는 null
     */
    private String findKing(ObjectNode pieces, boolean isWhite) {
        String kingType = isWhite ? "wK" : "bK";
        String[] positions = pieces.fieldNames().next().split(",");
        for (String pos : positions) {
            if (pieces.get(pos).asText().equals(kingType)) {
                return pos;
            }
        }
        return null;
    }

    /**
     * 한 위치에서 다른 위치로 공격이 가능한지 확인합니다.
     *
     * @param pieces 현재 보드의 기물 상태
     * @param fromPos 시작 위치
     * @param toPos 목표 위치
     * @param piece 기물
     * @return 공격 가능 여부
     */
    private boolean canAttack(ObjectNode pieces, String fromPos, String toPos, String piece) {
        // 각 기물 타입에 따른 공격 가능 여부 확인
        // 실제 구현에서는 각 기물의 이동 규칙에 따라 상세히 구현해야 함
        return false; // 임시 구현
    }

    /**
     * 초기 체스판 상태를 JSON 형식으로 생성합니다.
     *
     * @return JSON 문자열로 표현된 초기 체스판 상태
     */
    private String getInitialBoardState() {
        try {
            ObjectNode boardState = objectMapper.createObjectNode();
            ObjectNode pieces = objectMapper.createObjectNode();

            INITIAL_PIECES.forEach(pieces::put);
            boardState.set("pieces", pieces);

            return objectMapper.writeValueAsString(boardState);
        } catch (Exception e) {
            log.error("Failed to create initial board state", e);
            throw new RuntimeException("Failed to create initial board state", e);
        }
    }

    /**
     * 이동의 유효성을 검증합니다.
     * 현재 턴, 기물 이동 규칙, 특수 이동 규칙을 검사합니다.
     *
     * @param game 현재 게임
     * @param moveDTO 이동 정보
     * @param playerId 이동을 시도하는 플레이어의 ID
     * @throws InvalidTurnException 잘못된 턴일 경우
     * @throws IllegalMoveException 잘못된 이동일 경우
     */
    private void validateMove(ChessGame game, ChessMoveDTO moveDTO, Long playerId) {
        try {
            ObjectNode boardState = (ObjectNode) objectMapper.readTree(game.getBoardState());
            String currentPiece = boardState.get("pieces")
                    .get(moveDTO.getStartPosition()[0] + "," + moveDTO.getStartPosition()[1])
                    .asText();

            // 1. 턴 검증
            boolean isWhitePiece = currentPiece.startsWith("w");
            if (game.isWhiteTurn() != isWhitePiece) {
                throw new InvalidTurnException("Not your turn");
            }

            // 2. 기본 이동 규칙 검증
            validateBasicMove(moveDTO, currentPiece);

            // 3. 특수 이동 규칙 검증
            if (moveDTO.getSpecialMoves() != null) {
                validateSpecialMoves(game, moveDTO, boardState);
            }

        } catch (Exception e) {
            log.error("Move validation failed", e);
            throw new IllegalMoveException("Invalid move: " + e.getMessage());
        }
    }

    /**
     * 기본 이동 규칙을 검증합니다.
     *
     * @param moveDTO 이동 정보
     * @param piece 이동할 기물
     */
    private void validateBasicMove(ChessMoveDTO moveDTO, String piece) {
        int[] start = moveDTO.getStartPosition();
        int[] end = moveDTO.getEndPosition();
        int dx = Math.abs(end[0] - start[0]);
        int dy = Math.abs(end[1] - start[1]);

        switch (piece.charAt(1)) {
            case 'P': // Pawn
                validatePawnMove(moveDTO, piece.charAt(0) == 'w');
                break;
            case 'R': // Rook
                if (dx != 0 && dy != 0) {
                    throw new IllegalMoveException("Rook can only move horizontally or vertically");
                }
                break;
            case 'N': // Knight
                if (!((dx == 2 && dy == 1) || (dx == 1 && dy == 2))) {
                    throw new IllegalMoveException("Invalid knight move");
                }
                break;
            case 'B': // Bishop
                if (dx != dy) {
                    throw new IllegalMoveException("Bishop must move diagonally");
                }
                break;
            case 'Q': // Queen
                if (dx != dy && dx != 0 && dy != 0) {
                    throw new IllegalMoveException("Invalid queen move");
                }
                break;
            case 'K': // King
                if (dx > 1 || dy > 1) {
                    throw new IllegalMoveException("Invalid king move");
                }
                break;
        }
    }

    /**
     * 폰의 이동 규칙을 검증합니다.
     *
     * @param moveDTO 이동 정보
     * @param isWhite 백색 폰 여부
     */
    private void validatePawnMove(ChessMoveDTO moveDTO, boolean isWhite) {
        int[] start = moveDTO.getStartPosition();
        int[] end = moveDTO.getEndPosition();
        int direction = isWhite ? 1 : -1;
        int dy = end[1] - start[1];
        int dx = Math.abs(end[0] - start[0]);

        // 기본 전진
        if (dx == 0) {
            if (dy != direction && !(dy == 2 * direction && start[1] == (isWhite ? 1 : 6))) {
                throw new IllegalMoveException("Invalid pawn move");
            }
        }
        // 대각선 이동 (기물 잡기)
        else if (dx == 1 && dy == direction) {
            if (!moveDTO.getSpecialMoves().isTakePiece() && !moveDTO.getSpecialMoves().isEnpassant()) {
                throw new IllegalMoveException("Pawn can only move diagonally when capturing");
            }
        }
        else {
            throw new IllegalMoveException("Invalid pawn move");
        }
    }

    /**
     * 특수 이동 규칙을 검증합니다.
     *
     * @param game 현재 게임
     * @param moveDTO 이동 정보
     * @param boardState 현재 보드 상태
     */
    private void validateSpecialMoves(ChessGame game, ChessMoveDTO moveDTO, ObjectNode boardState) {
        // 캐슬링 검증
        if (moveDTO.getSpecialMoves().getCastling() != null) {
            validateCastling(game, moveDTO, boardState);
        }

        // 앙파상 검증
        if (moveDTO.getSpecialMoves().isEnpassant()) {
            validateEnPassant(game, moveDTO, boardState);
        }

        // 프로모션 검증
        if (moveDTO.getSpecialMoves().isPromotion()) {
            validatePromotion(moveDTO);
        }
    }

    /**
     * 게임 상태를 업데이트합니다.
     *
     * @param game 현재 게임
     * @param moveDTO 이동 정보
     */
    private void updateGameState(ChessGame game, ChessMoveDTO moveDTO) {
        try {
            ObjectNode boardState = (ObjectNode) objectMapper.readTree(game.getBoardState());
            ObjectNode pieces = (ObjectNode) boardState.get("pieces");

            // 기물 이동
            String startPos = moveDTO.getStartPosition()[0] + "," + moveDTO.getStartPosition()[1];
            String endPos = moveDTO.getEndPosition()[0] + "," + moveDTO.getEndPosition()[1];
            String piece = pieces.get(startPos).asText();

            // 특수 이동 처리
            if (moveDTO.getSpecialMoves() != null) {
                handleSpecialMoves(moveDTO, pieces, piece);
            }

            // 기본 이동 처리
            pieces.remove(startPos);
            pieces.put(endPos, piece);

            // 체크/체크메이트 상태 확인
            boolean isCheck = checkForCheck(pieces, !game.isWhiteTurn());
            game.setCheck(isCheck);

            if (isCheck && isCheckmate(pieces, !game.isWhiteTurn())) {
                game.setStatus(ChessGame.GameStatus.CHECKMATE);
            }

            // 보드 상태 저장
            game.setBoardState(objectMapper.writeValueAsString(boardState));
            game.setWhiteTurn(!game.isWhiteTurn());

        } catch (Exception e) {
            log.error("Failed to update game state", e);
            throw new RuntimeException("Failed to update game state", e);
        }
    }

    // ... (추가 헬퍼 메서드들은 필요에 따라 구현) ...
}