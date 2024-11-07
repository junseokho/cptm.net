package com.example.chessdotnet.service;

import com.example.chessdotnet.dto.ChessMoveDTO;
import com.example.chessdotnet.entity.ChessGame;
import com.example.chessdotnet.entity.Room;
import com.example.chessdotnet.exception.InvalidGameStateException;
import com.example.chessdotnet.repository.ChessGameRepository;
import com.example.chessdotnet.repository.RoomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 체스 게임의 핵심 로직을 처리하는 서비스 클래스입니다.
 * 게임 생성, 기물 이동, 게임 상태 관리 등의 기능을 제공합니다.
 *
 * @author 전종영
 * @version 1.2
 * @since 2024-11-07
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChessGameService {
    private final ChessGameRepository gameRepository;
    private final RoomRepository roomRepository;
    private final ObjectMapper objectMapper;

    /**
     * 프론트엔드에서 전송하는 초기 기물 정보를 담는 DTO 입니다.
     */
    @lombok.Data
    public static class InitialPieceDTO {
        /**
         * 기물의 정보 (종류, 색상)
         */
        private ChessMoveDTO.PieceInfo pieceInfo;

        /**
         * 기물의 초기 위치
         */
        private ChessMoveDTO.Position position;
    }

    /**
     * 프론트엔드로부터 받은 초기 기물 정보를 기반으로 게임을 생성합니다.
     *
     * @param roomId 게임을 생성할 방의 ID
     * @param initialPieces 초기 기물 배치 정보 리스트
     * @return 생성된 체스 게임
     * @throws InvalidGameStateException 방을 찾을 수 없는 경우
     */
    @Transactional
    public ChessGame createGame(Long roomId, List<InitialPieceDTO> initialPieces) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new InvalidGameStateException("Room not found"));

        ChessGame game = new ChessGame();
        game.setRoom(room);
        game.setBoardState(createInitialBoardState(initialPieces));
        game.setStartTime(new Date());
        game.setStatus(ChessGame.GameStatus.IN_PROGRESS);
        game.setWhiteTurn(true); // 게임 시작 시 흰색 차례

        log.info("Creating new chess game for room {}", roomId);
        return gameRepository.save(game);
    }

    /**
     * 프론트엔드에서 받은 초기 기물 정보를 바탕으로 보드 상태를 생성합니다.
     *
     * @param initialPieces 초기 기물 정보 리스트
     * @return JSON 형식의 초기 보드 상태
     */
    private String createInitialBoardState(List<InitialPieceDTO> initialPieces) {
        try {
            ObjectNode boardState = objectMapper.createObjectNode();
            ObjectNode pieces = objectMapper.createObjectNode();

            for (InitialPieceDTO piece : initialPieces) {
                String position = piece.getPosition().getRow() + "," + piece.getPosition().getCol();
                String pieceCode = getPieceCode(piece.getPieceInfo());
                pieces.put(position, pieceCode);
            }

            boardState.set("pieces", pieces);
            return objectMapper.writeValueAsString(boardState);
        } catch (Exception e) {
            log.error("Failed to create initial board state", e);
            throw new RuntimeException("Failed to create initial board state", e);
        }
    }

    /**
     * 기물 정보를 코드로 변환합니다.
     * 예: 흰색 폰 -> "wP", 검은색 킹 -> "bK"
     *
     * @param pieceInfo 기물 정보
     * @return 기물 코드
     * @throws IllegalArgumentException 알 수 없는 기물 종류인 경우
     */
    private String getPieceCode(ChessMoveDTO.PieceInfo pieceInfo) {
        String color = pieceInfo.getColor().toLowerCase().startsWith("w") ? "w" : "b";
        String type = switch (pieceInfo.getName().toLowerCase()) {
            case "pawn" -> "P";
            case "rook" -> "R";
            case "knight" -> "N";
            case "bishop" -> "B";
            case "queen" -> "Q";
            case "king" -> "K";
            default -> throw new IllegalArgumentException("Unknown piece type: " + pieceInfo.getName());
        };
        return color + type;
    }

    /**
     * 체스 기물을 이동시킵니다.
     * 현재는 기본적인 이동만 처리하며, 이동 규칙 검증은 수행하지 않습니다.
     *
     * @param roomId 게임이 진행 중인 방의 ID
     * @param moveDTO 이동 정보
     * @param playerId 이동을 시도하는 플레이어의 ID
     * @return 업데이트된 체스 게임
     * @throws InvalidGameStateException 유효하지 않은 게임 상태이거나 이동 처리 실패 시
     */
    @Transactional
    public ChessGame doMove(Long roomId, ChessMoveDTO moveDTO, Long playerId) {
        ChessGame game = gameRepository.findByRoom_IdAndStatus(roomId, ChessGame.GameStatus.IN_PROGRESS)
                .orElseThrow(() -> new InvalidGameStateException("No active game found"));

        try {
            // 현재 보드 상태 로드
            ObjectNode boardState = (ObjectNode) objectMapper.readTree(game.getBoardState());
            ObjectNode pieces = (ObjectNode) boardState.get("pieces");

            // 이동 실행
            String startPos = moveDTO.getStartPosition().getRow() + "," + moveDTO.getStartPosition().getCol();
            String endPos = moveDTO.getEndPosition().getRow() + "," + moveDTO.getEndPosition().getCol();

            // 기물 이동
            if (pieces.has(startPos)) {
                String piece = pieces.get(startPos).asText();
                pieces.remove(startPos);
                pieces.put(endPos, piece);

                log.debug("Moving piece from {} to {}: {}", startPos, endPos, piece);
            } else {
                log.warn("No piece found at position: {}", startPos);
            }

            // 보드 상태 업데이트
            game.setBoardState(objectMapper.writeValueAsString(boardState));
            game.setWhiteTurn(!game.isWhiteTurn());
            game.setLastMoveTime(new Date());

            // 게임 상태 체크 (향후 확장용)
            game.setStatus(checkGameStatus(game));

            return gameRepository.save(game);

        } catch (Exception e) {
            log.error("Failed to process move", e);
            throw new InvalidGameStateException("Failed to process move: " + e.getMessage());
        }
    }

    /**
     * 게임의 상태를 확인합니다.
     * 현재는 기본적인 상태만 반환하며, 향후 체크/체크메이트 등의 상태 확인 로직이 추가될 예정입니다.
     *
     * @param game 상태를 확인할 게임
     * @return 게임의 현재 상태
     */
    private ChessGame.GameStatus checkGameStatus(ChessGame game) {
        // TODO: 향후 체크, 체크메이트, 스테일메이트 등의 상태 확인 로직 구현
        return ChessGame.GameStatus.IN_PROGRESS;
    }
}