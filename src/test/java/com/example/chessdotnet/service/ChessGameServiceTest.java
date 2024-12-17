//package com.example.chessdotnet.service;
//
//import com.example.chessdotnet.dto.ChessMoveDTO;
//import com.example.chessdotnet.entity.ChessGame;
//import com.example.chessdotnet.entity.Room;
//import com.example.chessdotnet.exception.IllegalMoveException;
//import com.example.chessdotnet.exception.InvalidGameStateException;
//import com.example.chessdotnet.exception.InvalidTurnException;
//import com.example.chessdotnet.repository.ChessGameRepository;
//import com.example.chessdotnet.repository.RoomRepository;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Spy;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
///**
// * ChessGameService 클래스에 대한 단위 테스트를 수행합니다.
// * 체스 게임의 기본 규칙과 특수 규칙에 대한 테스트를 포함합니다.
// *
// * @author 전종영
// * @version 1.0
// * @since 2024-11-05
// */
//@ExtendWith(MockitoExtension.class)
//public class ChessGameServiceTest {
//
//    @Mock
//    private ChessGameRepository gameRepository;
//
//    @Mock
//    private RoomRepository roomRepository;
//
//    @Spy
//    private ObjectMapper objectMapper = new ObjectMapper();
//
//    @InjectMocks
//    private ChessGameService chessGameService;
//
//    private ChessGame testGame;
//    private Room testRoom;
//
//    /**
//     * 테스트를 위한 보드 상태를 초기화하는 헬퍼 메서드입니다.
//     *
//     * @author 전종영
//     * @param pieces 기물의 위치와 종류를 담은 맵
//     * @return 초기화된 보드 상태 JSON 문자열
//     */
//    private String initializeBoardState(Map<String, String> pieces) {
//        try {
//            ObjectNode boardState = objectMapper.createObjectNode();
//            ObjectNode piecesNode = objectMapper.createObjectNode();
//
//            // 기본 킹 위치 설정 (필수)
//            piecesNode.put("4,0", "wK");
//            piecesNode.put("4,7", "bK");
//
//            // 추가 기물 위치 설정
//            pieces.forEach(piecesNode::put);
//
//            boardState.set("pieces", piecesNode);
//            return objectMapper.writeValueAsString(boardState);
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to initialize board state", e);
//        }
//    }
//
//    /**
//     * 테스트를 위한 게임 상태를 설정하는 헬퍼 메서드입니다.
//     *
//     * @author 전종영
//     */
//    @BeforeEach
//    void setUp() {
//        testRoom = new Room();
//        testRoom.setId(1L);
//
//        testGame = new ChessGame();
//        testGame.setId(1L);
//        testGame.setRoom(testRoom);
//        testGame.setWhiteTurn(true);
//        testGame.setStatus(ChessGame.GameStatus.IN_PROGRESS);
//
//        // 기본 보드 상태 설정
//        Map<String, String> initialPieces = new HashMap<>();
//        // 테스트에 필요한 기물 추가
//        testGame.setBoardState(initializeBoardState(initialPieces));
//
//        // 기본 Mocking 설정
//        when(gameRepository.findByRoom_IdAndStatus(anyLong(), any()))
//                .thenReturn(Optional.of(testGame));
//        when(gameRepository.save(any(ChessGame.class)))
//                .thenAnswer(invocation -> invocation.getArgument(0));
//    }
//
//    /**
//     * 테스트별 특수한 보드 상태를 설정하는 메서드입니다.
//     *
//     * @author 전종영
//     */
//    void setupGameForPawnMove() {
//        Map<String, String> pieces = new HashMap<>();
//        pieces.put("1,1", "wP");  // 테스트할 폰 위치
//        testGame.setBoardState(initializeBoardState(pieces));
//
//        when(gameRepository.findByRoom_IdAndStatus(anyLong(), any()))
//                .thenReturn(Optional.of(testGame));
//        when(gameRepository.save(any(ChessGame.class)))
//                .thenAnswer(invocation -> invocation.getArgument(0));
//    }
//
//    /**
//     * 폰 이동 관련 테스트 그룹입니다.
//     */
//    @Nested
//    @DisplayName("폰 이동 테스트")
//    class PawnMoveTests {
//
//        /**
//         * 폰의 기본 전진 이동을 테스트합니다.
//         */
//        @Test
//        @DisplayName("폰 기본 전진")
//        void pawnBasicMove() {
//            // Given
//            ChessMoveDTO moveDTO = createMoveDTO(1, 1, 1, 2, "wP");
//            setupGameForMove();
//
//            // When & Then
//            assertDoesNotThrow(() -> chessGameService.doMove(1L, moveDTO, 1L));
//        }
//
//        /**
//         * 폰의 첫 이동 시 2칸 전진을 테스트합니다.
//         */
//        @Test
//        @DisplayName("폰 첫 이동 2칸 전진")
//        void pawnFirstMove() {
//            // Given
//            ChessMoveDTO moveDTO = createMoveDTO(1, 1, 1, 3, "wP");
//            setupGameForMove();
//
//            // When & Then
//            assertDoesNotThrow(() -> chessGameService.doMove(1L, moveDTO, 1L));
//        }
//
//        /**
//         * 폰의 대각선 공격 이동을 테스트합니다.
//         */
//        @Test
//        @DisplayName("폰 대각선 공격")
//        void pawnDiagonalCapture() {
//            // Given
//            ChessMoveDTO moveDTO = createMoveDTO(1, 1, 2, 2, "wP");
//            moveDTO.getSpecialMoves().setTakePiece(true);
//            setupGameForMove();
//
//            // When & Then
//            assertDoesNotThrow(() -> chessGameService.doMove(1L, moveDTO, 1L));
//        }
//    }
//
//    /**
//     * 특수 이동 관련 테스트 그룹입니다.
//     */
//    @Nested
//    @DisplayName("특수 이동 테스트")
//    class SpecialMoveTests {
//
//        /**
//         * 킹사이드 캐슬링을 테스트합니다.
//         */
//        @Test
//        @DisplayName("킹사이드 캐슬링")
//        void kingSideCastling() {
//            // Given
//            ChessMoveDTO moveDTO = createCastlingMoveDTO(true);
//            setupGameForCastling();
//
//            // When & Then
//            assertDoesNotThrow(() -> chessGameService.doMove(1L, moveDTO, 1L));
//        }
//
//        /**
//         * 앙파상을 테스트합니다.
//         */
//        @Test
//        @DisplayName("앙파상")
//        void enPassant() {
//            // Given
//            ChessMoveDTO moveDTO = createEnPassantMoveDTO();
//            setupGameForEnPassant();
//
//            // When & Then
//            assertDoesNotThrow(() -> chessGameService.doMove(1L, moveDTO, 1L));
//        }
//
//        /**
//         * 폰의 프로모션을 테스트합니다.
//         */
//        @Test
//        @DisplayName("폰 프로모션")
//        void pawnPromotion() {
//            // Given
//            ChessMoveDTO moveDTO = createPromotionMoveDTO();
//            setupGameForPromotion();
//
//            // When & Then
//            assertDoesNotThrow(() -> chessGameService.doMove(1L, moveDTO, 1L));
//        }
//    }
//
//    /**
//     * 게임 상태 관련 테스트 그룹입니다.
//     */
//    @Nested
//    @DisplayName("게임 상태 테스트")
//    class GameStateTests {
//
//        /**
//         * 체크 상태를 테스트합니다.
//         */
//        @Test
//        @DisplayName("체크 상태 확인")
//        void checkState() {
//            // Given
//            ChessMoveDTO moveDTO = createCheckMoveDTO();
//            setupGameForCheck();
//
//            // When
//            ChessGame result = chessGameService.doMove(1L, moveDTO, 1L);
//
//            // Then
//            assertTrue(result.isCheck());
//        }
//
//        /**
//         * 체크메이트 상태를 테스트합니다.
//         */
//        @Test
//        @DisplayName("체크메이트 상태 확인")
//        void checkmateState() {
//            // Given
//            ChessMoveDTO moveDTO = createCheckmateMoveDTO();
//            setupGameForCheckmate();
//
//            // When
//            ChessGame result = chessGameService.doMove(1L, moveDTO, 1L);
//
//            // Then
//            assertEquals(ChessGame.GameStatus.CHECKMATE, result.getStatus());
//        }
//    }
//
//    /**
//     * 잘못된 이동에 대한 테스트 그룹입니다.
//     */
//    @Nested
//    @DisplayName("잘못된 이동 테스트")
//    class InvalidMoveTests {
//
//        /**
//         * 잘못된 턴에 이동을 시도할 때를 테스트합니다.
//         */
//        @Test
//        @DisplayName("잘못된 턴 이동")
//        void wrongTurnMove() {
//            // Given
//            ChessMoveDTO moveDTO = createMoveDTO(0, 1, 0, 2, "bP");
//            setupGameForMove();
//
//            // When & Then
//            assertThrows(InvalidTurnException.class,
//                    () -> chessGameService.doMove(1L, moveDTO, 1L));
//        }
//
//        /**
//         * 룩의 잘못된 이동을 테스트합니다.
//         */
//        @Test
//        @DisplayName("룩 잘못된 이동")
//        void invalidRookMove() {
//            // Given
//            ChessMoveDTO moveDTO = createMoveDTO(0, 0, 1, 1, "wR");
//            setupGameForMove();
//
//            // When & Then
//            assertThrows(IllegalMoveException.class,
//                    () -> chessGameService.doMove(1L, moveDTO, 1L));
//        }
//    }
//
//    // Helper methods
//    private ChessMoveDTO createMoveDTO(int startX, int startY, int endX, int endY, String pieceType) {
//        ChessMoveDTO moveDTO = new ChessMoveDTO();
//        moveDTO.setStartPosition(new int[]{startX, startY});
//        moveDTO.setEndPosition(new int[]{endX, endY});
//        moveDTO.setPieceType(pieceType);
//        moveDTO.setSpecialMoves(new ChessMoveDTO.SpecialMoves());
//        return moveDTO;
//    }
//
//    private ChessMoveDTO createCastlingMoveDTO(boolean isKingSide) {
//        ChessMoveDTO moveDTO = createMoveDTO(4, 0, isKingSide ? 6 : 2, 0, "wK");
//        ChessMoveDTO.SpecialMoves specialMoves = new ChessMoveDTO.SpecialMoves();
//        ChessMoveDTO.Castling castling = new ChessMoveDTO.Castling();
//        castling.setKingSide(isKingSide);
//        specialMoves.setCastling(castling);
//        moveDTO.setSpecialMoves(specialMoves);
//        return moveDTO;
//    }
//
//    private ChessMoveDTO createEnPassantMoveDTO() {
//        ChessMoveDTO moveDTO = createMoveDTO(3, 4, 4, 5, "wP");
//        ChessMoveDTO.SpecialMoves specialMoves = new ChessMoveDTO.SpecialMoves();
//        specialMoves.setEnpassant(true);
//        specialMoves.setTakenPiecePosition(new int[]{4, 4});
//        moveDTO.setSpecialMoves(specialMoves);
//        return moveDTO;
//    }
//
//    private ChessMoveDTO createPromotionMoveDTO() {
//        ChessMoveDTO moveDTO = createMoveDTO(1, 6, 1, 7, "wP");
//        ChessMoveDTO.SpecialMoves specialMoves = new ChessMoveDTO.SpecialMoves();
//        specialMoves.setPromotion(true);
//        specialMoves.setPromotionToWhat("wQ");
//        moveDTO.setSpecialMoves(specialMoves);
//        return moveDTO;
//    }
//
//    private ChessMoveDTO createCheckMoveDTO() {
//        // 체크 상황을 만드는 이동
//        return createMoveDTO(3, 1, 3, 7, "wQ");
//    }
//
//    private ChessMoveDTO createCheckmateMoveDTO() {
//        // 체크메이트 상황을 만드는 이동
//        return createMoveDTO(7, 1, 7, 7, "wQ");
//    }
//
//    /**
//     * 기본 이동을 위한 상태 설정
//     */
//    private void setupGameForMove() {
//        // 기본 Mocking 설정
//        when(gameRepository.findByRoom_IdAndStatus(anyLong(), any()))
//                .thenReturn(Optional.of(testGame));
//        when(gameRepository.save(any(ChessGame.class)))
//                .thenAnswer(invocation -> invocation.getArgument(0));
//    }
//
//
//    /**
//     * 캐슬링을 위한 보드 상태 설정
//     */
//    private void setupGameForCastling() {
//        ObjectNode boardState = objectMapper.createObjectNode();
//        ObjectNode pieces = objectMapper.createObjectNode();
//
//        // 기본 킹 위치 (모든 테스트에 필수)
//        pieces.put("4,0", "wK");
//        pieces.put("4,7", "bK");
//        // 캐슬링을 위한 룩 위치
//        pieces.put("7,0", "wR");
//
//        boardState.set("pieces", pieces);
//
//        try {
//            testGame.setBoardState(objectMapper.writeValueAsString(boardState));
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to setup castling state", e);
//        }
//
//        setupGameForMove();
//    }
//
//    /**
//     * 앙파상을 위한 보드 상태 설정
//     */
//    private void setupGameForEnPassant() {
//        ObjectNode boardState = objectMapper.createObjectNode();
//        ObjectNode pieces = objectMapper.createObjectNode();
//
//        // 기본 킹 위치 (모든 테스트에 필수)
//        pieces.put("4,0", "wK");
//        pieces.put("4,7", "bK");
//        // 앙파상 상황 설정
//        pieces.put("3,4", "wP");
//        pieces.put("4,4", "bP");
//
//        boardState.set("pieces", pieces);
//
//        try {
//            testGame.setBoardState(objectMapper.writeValueAsString(boardState));
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to setup en passant state", e);
//        }
//
//        setupGameForMove();
//    }
//
//    /**
//     * 프로모션을 위한 보드 상태 설정
//     */
//    private void setupGameForPromotion() {
//        ObjectNode boardState = objectMapper.createObjectNode();
//        ObjectNode pieces = objectMapper.createObjectNode();
//
//        // 기본 킹 위치 (모든 테스트에 필수)
//        pieces.put("4,0", "wK");
//        pieces.put("4,7", "bK");
//        // 프로모션 직전의 폰
//        pieces.put("1,6", "wP");
//
//        boardState.set("pieces", pieces);
//
//        try {
//            testGame.setBoardState(objectMapper.writeValueAsString(boardState));
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to setup promotion state", e);
//        }
//
//        setupGameForMove();
//    }
//
//    /**
//     * 체크 상태를 위한 보드 상태 설정
//     */
//    private void setupGameForCheck() {
//        ObjectNode boardState = objectMapper.createObjectNode();
//        ObjectNode pieces = objectMapper.createObjectNode();
//
//        // 기본 킹 위치 (모든 테스트에 필수)
//        pieces.put("4,0", "wK");
//        pieces.put("4,7", "bK");
//        // 체크 상황을 만들 퀸
//        pieces.put("3,1", "wQ");
//
//        boardState.set("pieces", pieces);
//
//        try {
//            testGame.setBoardState(objectMapper.writeValueAsString(boardState));
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to setup check state", e);
//        }
//
//        setupGameForMove();
//    }
//
//    /**
//     * 체크메이트 상태를 위한 보드 상태 설정
//     */
//    private void setupGameForCheckmate() {
//        ObjectNode boardState = objectMapper.createObjectNode();
//        ObjectNode pieces = objectMapper.createObjectNode();
//
//        // 기본 킹 위치 (모든 테스트에 필수)
//        pieces.put("4,0", "wK");
//        pieces.put("4,7", "bK");
//        // 체크메이트 상황을 만들 기물들
//        pieces.put("7,1", "wQ");
//        pieces.put("6,7", "wR");
//
//        boardState.set("pieces", pieces);
//
//        try {
//            testGame.setBoardState(objectMapper.writeValueAsString(boardState));
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to setup checkmate state", e);
//        }
//
//        setupGameForMove();
//    }
//
//    /**
//     * 잘못된 턴 테스트를 위한 보드 상태 설정
//     */
//    private void setupGameForWrongTurn() {
//        ObjectNode boardState = objectMapper.createObjectNode();
//        ObjectNode pieces = objectMapper.createObjectNode();
//
//        // 기본 킹 위치 (모든 테스트에 필수)
//        pieces.put("4,0", "wK");
//        pieces.put("4,7", "bK");
//        // 잘못된 턴의 폰
//        pieces.put("0,1", "bP");
//
//        boardState.set("pieces", pieces);
//
//        try {
//            testGame.setBoardState(objectMapper.writeValueAsString(boardState));
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to setup wrong turn state", e);
//        }
//
//        setupGameForMove();
//    }
//}