package com.example.chessdotnet.service.chessGameSession;

import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;
import org.springframework.util.Assert;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.TreeSet;

public class ChessboardTest {

    /* Algebraic chess notation to ChessboardPos */
    static public ChessboardPos parsePos(String pos) {
        int col = (int)(pos.charAt(0) - 'a');

        int row = (int)(pos.charAt(1) - '0');
        row -= 1;
        row = 7 - row;

        return new ChessboardPos(row, col);
    }

    /* Algebraic chess notation(not standard) to ChessboardMove
     * format is `(src) (dest)` (e.g. e2 e4 )
     */
    static public ChessboardMoveForTest parseMove(String move) {
        return new ChessboardMoveForTest(
                parsePos(move.substring(0, 2)),
                parsePos(move.substring(3))
        );
    }

    /* Having revertible move method. */
    static class ChessboardForTest extends Chessboard {
        public ChessboardForTest() {
            super();
        }
        public boolean tryMoveAndRollback(ChessboardMoveForTest move) {
            var src = move.startPosition;
            var dest = move.endPosition;

            Piece srcPiece = getPiece(src);
            Piece destPiece = getPiece(src);
            var befo_nullable_lastMoved = nullable_lastMoved;

            var ret = tryMovePiece(new ChessboardMove(src, dest));
            if (ret) {
                nullable_lastMoved = befo_nullable_lastMoved;
                chessboard[src.row][src.col] = srcPiece;
                chessboard[dest.row][dest.col] = destPiece;

                if (turnNow == Piece.PieceColor.BLACK) {
                    turnNow = Piece.PieceColor.WHITE;
                } else {
                    turnNow = Piece.PieceColor.BLACK;
                }

                moveRecords.removeLast();

                if (move.isCastling()) {
                    ChessboardPos castleDirection = ChessboardPos.sub(dest, src);
                    int colOfRook;
                    if (castleDirection.col > 0) {
                        colOfRook = 7;
                    } else {
                        colOfRook = 0;
                    }

                    int rowOfRook = dest.row;

                    ChessboardPos newPosOfRook = new ChessboardPos(
                            rowOfRook,
                            castleDirection.col > 0 ? dest.col - 1 : dest.col + 1
                    );

                    chessboard[rowOfRook][colOfRook] = chessboard[newPosOfRook.row][newPosOfRook.col];
                    chessboard[rowOfRook][colOfRook].position = new ChessboardPos(rowOfRook, colOfRook);
                    chessboard[newPosOfRook.row][newPosOfRook.col] = new Piece(
                            new ChessboardPos(rowOfRook, colOfRook),
                            Piece.PieceColor.NONE,
                            this
                    );
                }

                if (move.getEnPassantInfo().getFirst()) {
                    ChessboardPos pawnTaken = move.getEnPassantInfo().getSecond();
                    chessboard[pawnTaken.row][pawnTaken.col] = new Pawn(
                            new ChessboardPos(pawnTaken),
                            (turnNow == Piece.PieceColor.BLACK) ? Piece.PieceColor.WHITE : Piece.PieceColor.BLACK,
                            this,
                            (turnNow == Piece.PieceColor.BLACK) ? new ChessboardPos(1, 0) : new ChessboardPos(-1, 0)
                    );
                }
            }
            return ret;
        }
    }

    static class ChessboardMoveForTest extends ChessboardMove {
        public ChessboardMoveForTest(ChessboardPos src, ChessboardPos dest) { super(src, dest); }
        public boolean isLegal = true;
        public ChessboardMoveForTest clearIsLegal() { isLegal = false; return this; }
    }

    static public void showBoardBrief(Chessboard board, ChessboardPos src, ChessboardPos dest) {
        System.out.println("[Board state]\n" +
                "Turn:\t\t\t\t" + board.turnNow.name +
                "\nLast moved:\t\t\t" + board.nullable_lastMoved.toString() +
                "\nLast piece:\t\t\t" + board.moveRecords.getLast().toString() +
                "\nPiece in dest:\t\t" + board.getPiece(dest).toString() +
                "\nPiece in src:\t\t" + board.getPiece(src).toString() +
                "\nPassed move count:\t" + board.moveRecords.size()
        );
    }

    /* Simulate all moves, checking `isLegal == tryMovePiece` */
    Chessboard runMoves(LinkedList<ChessboardMoveForTest> moves) {
        Chessboard board = new Chessboard();

        for (var move : moves) {
            boolean res = board.tryMovePiece(move);
            if (move.isLegal != res) {
                showBoardBrief(board, move.startPosition, move.endPosition);
                Assert.isTrue(false, res ? "False Positive" : "False Negative");
            }
        }
        return board;
    }

    /* Comparator for TreeSet<ChessboardMove> */
    static Comparator<ChessboardMove> moveComparator = new Comparator<ChessboardMove>() {
        public int compareChessboardPos(ChessboardPos o1, ChessboardPos o2) {
            if (o1.row != o2.row) return Integer.compare(o1.row, o2.row);
            else return Integer.compare(o1.col, o2.col);
        }
        @Override
        public int compare(ChessboardMove o1, ChessboardMove o2) {
            if (!o1.startPosition.equals(o2.startPosition))
                return compareChessboardPos(o1.startPosition, o2.startPosition);
            else
                return compareChessboardPos(o1.endPosition, o2.endPosition);
        }
    };



    @Test
    void testMovesFromInitial() {
        TreeSet<ChessboardMove> whiteLegalMoves = new TreeSet<>(moveComparator);
        TreeSet<ChessboardMove> blackLegalMoves = new TreeSet<>(moveComparator);

        /* Pawn lines */
        for (int c = 0; c < 8; c++) {
            ChessboardMove move = new ChessboardMove(
                    new ChessboardPos(1, c),
                    new ChessboardPos(2, c)
            );
            blackLegalMoves.add(move);

            move = new ChessboardMove(
                    new ChessboardPos(1, c),
                    new ChessboardPos(3, c)
            );
            blackLegalMoves.add(move);
        }

        for (int c = 0; c < 8; c++) {
            ChessboardMove move = new ChessboardMove(
                    new ChessboardPos(6, c),
                    new ChessboardPos(5, c)
            );
            whiteLegalMoves.add(move);

            move = new ChessboardMove(
                    new ChessboardPos(6, c),
                    new ChessboardPos(4, c)
            );
            whiteLegalMoves.add(move);
        }

        /* Knight 1 */
        blackLegalMoves.add(new ChessboardMove(
                new ChessboardPos(0, 1),
                new ChessboardPos(2, 0)
        ));
        blackLegalMoves.add(new ChessboardMove(
                new ChessboardPos(0, 1),
                new ChessboardPos(2, 2)
        ));

        /* Knight 2 */
        blackLegalMoves.add(new ChessboardMove(
                new ChessboardPos(0, 6),
                new ChessboardPos(2, 7)
        ));
        blackLegalMoves.add(new ChessboardMove(
                new ChessboardPos(0, 6),
                new ChessboardPos(2, 5)
        ));

        /* Knight 3 */
        whiteLegalMoves.add(new ChessboardMove(
                new ChessboardPos(7, 1),
                new ChessboardPos(5, 0)
        ));
        whiteLegalMoves.add(new ChessboardMove(
                new ChessboardPos(7, 1),
                new ChessboardPos(5, 2)
        ));

        /* Knight 4 */
        whiteLegalMoves.add(new ChessboardMove(
                new ChessboardPos(7, 6),
                new ChessboardPos(5, 5)
        ));
        whiteLegalMoves.add(new ChessboardMove(
                new ChessboardPos(7, 6),
                new ChessboardPos(5, 7)
        ));


        /* for all squares, start and destinations */
        for (int sr = 0; sr < 8; sr++) {
            for (int sc = 0; sc < 8; sc++) {
                for (int dr = 0; dr < 8; dr++) {
                    for (int dc = 0; dc < 8; dc++) {
                        ChessboardForTest board = new ChessboardForTest();
                        ChessboardPos src = new ChessboardPos(sr, sc);
                        ChessboardPos dest = new ChessboardPos(dr, dc);
                        ChessboardMove move = new ChessboardMove(src, dest);
                        if (board.tryMovePiece(new ChessboardMove(src, dest))) {
                            Assert.isTrue(whiteLegalMoves.contains(move),
                                    String.format("[%d,%d]-[%d,%d] False Positive at white turn",
                                            sr, sc, dr, dc));
                        } else {
                            Assert.isTrue(!whiteLegalMoves.contains(move),
                                    String.format("[%d,%d]-[%d,%d] False Negative at white turn",
                                            sr, sc, dr, dc));
                        }
                    }
                }
            }
        }



        /* for all squares, start and destinations */
        for (int sr = 0; sr < 8; sr++) {
            for (int sc = 0; sc < 8; sc++) {
                for (int dr = 0; dr < 8; dr++) {
                    for (int dc = 0; dc < 8; dc++) {
                        ChessboardPos src = new ChessboardPos(sr, sc);
                        ChessboardPos dest = new ChessboardPos(dr, dc);
                        ChessboardMove move = new ChessboardMove(src, dest);
                        ChessboardForTest blackTurn = new ChessboardForTest();
                        blackTurn.turnNow = Piece.PieceColor.BLACK;
                        if (blackTurn.tryMovePiece(new ChessboardMove(src, dest))) {
                            if (!blackLegalMoves.contains(move))
                                System.out.println("board state\n" +
                                        "Turn:\t\t\t" + blackTurn.turnNow.name +
                                        "\nlast moved:\t\t" + blackTurn.nullable_lastMoved.toString() +
                                        "\nlast piece:\t\t" + blackTurn.moveRecords.getLast().toString() +
                                        "\npiece in dest:\t" + blackTurn.getPiece(dest).toString() +
                                        "\npiece in src:\t" + blackTurn.getPiece(src).toString());
                            Assert.isTrue(blackLegalMoves.contains(move),
                                    String.format("[%d,%d]-[%d,%d] False Positive at black turn",
                                            sr, sc, dr, dc));
                        } else {
                            if (blackLegalMoves.contains(move))
                                System.out.println("board state\n" +
                                        "Turn:\t\t\t" + blackTurn.turnNow.name +
                                        "\nlast moved:\t\t" + blackTurn.nullable_lastMoved.toString() +
                                        "\nlast piece:\t\t" + blackTurn.moveRecords.getLast().toString() +
                                        "\npiece in dest:\t" + blackTurn.getPiece(dest).toString() +
                                        "\npiece in src:\t" + blackTurn.getPiece(src).toString());
                            Assert.isTrue(!blackLegalMoves.contains(move),
                                    String.format("[%d,%d]-[%d,%d] False Negative at black turn",
                                            sr, sc, dr, dc));
                        }
                    }
                }
            }
        }
    }



    @Test
    void kingSideCastlingLegal() {
        LinkedList<ChessboardMoveForTest> moves = new LinkedList<>();

        moves.add(parseMove("e2 e4"));
        moves.add(parseMove("e7 e5"));
        moves.add(parseMove("g1 f3"));
        moves.add(parseMove("b8 c6"));
        moves.add(parseMove("f1 c4"));
        moves.add(parseMove("f8 c5"));
        moves.add(parseMove("e1 g1"));
        moves.add(parseMove("g8 f6"));
        moves.add(parseMove("f1 e1"));
        runMoves(moves);
    }

    @Test
    void kingSideCastlingIllegal_ThreatOnWay() {
        LinkedList<ChessboardMoveForTest> moves = new LinkedList<>();

        moves.add(parseMove("e2 e4"));
        moves.add(parseMove("d7 d5"));
        moves.add(parseMove("f1 a6"));
        moves.add(parseMove("d8 d6"));
        moves.add(parseMove("g1 f3"));
        moves.add(parseMove("d6 a6"));
        moves.add(parseMove("e1 g1").clearIsLegal());
        runMoves(moves);
    }

    @Test
    void kingSideCastlingIllegal_KingMoved() {
        LinkedList<ChessboardMoveForTest> moves = new LinkedList<>();

        moves.add(parseMove("e2 e4"));
        moves.add(parseMove("d7 d5"));
        moves.add(parseMove("f1 a6"));
        moves.add(parseMove("d8 d6"));
        moves.add(parseMove("g1 f3"));
        moves.add(parseMove("d6 h6"));
        moves.add(parseMove("e1 e2"));
        moves.add(parseMove("h6 d6"));
        moves.add(parseMove("e2 e1"));
        moves.add(parseMove("d6 d8"));
        moves.add(parseMove("e1 g1").clearIsLegal());
        runMoves(moves);
    }

    @Test
    void kingSideCastlingIllegal_RookMoved() {
        LinkedList<ChessboardMoveForTest> moves = new LinkedList<>();

        moves.add(parseMove("e2 e4"));
        moves.add(parseMove("d7 d5"));
        moves.add(parseMove("f1 a6"));
        moves.add(parseMove("d8 d6"));
        moves.add(parseMove("g1 f3"));
        moves.add(parseMove("d6 h6"));
        moves.add(parseMove("h1 g1"));
        moves.add(parseMove("h6 d6"));
        moves.add(parseMove("g1 h1"));
        moves.add(parseMove("d6 d8"));
        moves.add(parseMove("e1 g1").clearIsLegal());
        runMoves(moves);
    }

    @Test
    void queenSideCastlingLegal() {
        LinkedList<ChessboardMoveForTest> moves = new LinkedList<>();

        moves.add(parseMove("d2 d4"));
        moves.add(parseMove("d7 d5"));
        moves.add(parseMove("c1 h6"));
        moves.add(parseMove("g8 h6"));
        moves.add(parseMove("b1 c3"));
        moves.add(parseMove("d8 d6"));
        moves.add(parseMove("d1 d3"));
        moves.add(parseMove("d6 g6"));
        moves.add(parseMove("e1 c1"));
        moves.add(parseMove("g6 d6"));
        moves.add(parseMove("d1 d2"));
        runMoves(moves);
    }

    @Test
    void queenSideCastlingIllegal_ThreatOnWay() {
        LinkedList<ChessboardMoveForTest> moves = new LinkedList<>();

        moves.add(parseMove("d2 d4"));
        moves.add(parseMove("d7 d5"));
        moves.add(parseMove("c1 h6"));
        moves.add(parseMove("g8 h6"));
        moves.add(parseMove("b1 c3"));
        moves.add(parseMove("d8 d6"));
        moves.add(parseMove("d1 d3"));
        moves.add(parseMove("d6 f4"));
        moves.add(parseMove("e1 c1").clearIsLegal());
        runMoves(moves);
    }

    @Test
    void enPassantLegal() {
        LinkedList<ChessboardMoveForTest> moves = new LinkedList<>();

        moves.add(parseMove("e2 e4"));
        moves.add(parseMove("b8 c6"));
        moves.add(parseMove("e4 e5"));
        moves.add(parseMove("d7 d5"));
        moves.add(parseMove("e5 d6"));

        runMoves(moves);
    }

    @Test
    void enPassantIllegal_NoTarget() {
        LinkedList<ChessboardMoveForTest> moves = new LinkedList<>();

        moves.add(parseMove("e2 e4"));
        moves.add(parseMove("b8 c6"));
        moves.add(parseMove("e4 e5"));
        moves.add(parseMove("c6 b8"));
        moves.add(parseMove("e5 d6").clearIsLegal());
        runMoves(moves);

        /* Another Case: target is already moved */
        moves = new LinkedList<>();

        moves.add(parseMove("e2 e4"));
        moves.add(parseMove("b8 c6"));
        moves.add(parseMove("e4 e5"));
        moves.add(parseMove("d7 d5"));
        moves.add(parseMove("g1 f3"));
        moves.add(parseMove("d5 d4"));
        moves.add(parseMove("e5 d6").clearIsLegal());
    }

    @Test
    void enPassantIllegal_TooLate() {
        LinkedList<ChessboardMoveForTest> moves = new LinkedList<>();

        moves.add(parseMove("e2 e4"));
        moves.add(parseMove("b8 c6"));
        moves.add(parseMove("e4 e5"));
        moves.add(parseMove("d7 d5"));
        moves.add(parseMove("g1 f3"));
        moves.add(parseMove("c6 b8"));
        moves.add(parseMove("e5 d6").clearIsLegal());
        runMoves(moves);
    }

    @Test
    void moveIllegal_NotKingInCheck() {
        LinkedList<ChessboardMoveForTest> moves = new LinkedList<>();

        moves.add(parseMove("e2 e4"));
        moves.add(parseMove("e7 e5"));
        moves.add(parseMove("d1 h5"));
        moves.add(parseMove("g8 f6"));
        moves.add(parseMove("h5 e5"));
        moves.add(parseMove("f6 g4").clearIsLegal());
        runMoves(moves);
    }

    @Test
    void moveLegal_blockCheck() {
        LinkedList<ChessboardMoveForTest> moves = new LinkedList<>();

        moves.add(parseMove("e2 e4"));
        moves.add(parseMove("e7 e5"));
        moves.add(parseMove("d1 h5"));
        moves.add(parseMove("g8 f6"));
        moves.add(parseMove("h5 e5"));
        moves.add(parseMove("f8 e7"));
        runMoves(moves);
    }

    @Test
    void moveLegal_evadeCheck() {
        LinkedList<ChessboardMoveForTest> moves = new LinkedList<>();

        moves.add(parseMove("e2 e4"));
        moves.add(parseMove("e7 e5"));
        moves.add(parseMove("d1 h5"));
        moves.add(parseMove("d7 d5"));
        moves.add(parseMove("h5 e5"));
        moves.add(parseMove("e8 d7"));
        runMoves(moves);
    }

    /* Check the promotion properly reverted  */
    @Test
    void moveILLegal_noEvadeCheckButPromotion() {
        LinkedList<ChessboardMoveForTest> moves = new LinkedList<>();

        moves.add(parseMove("d2 d4"));
        moves.add(parseMove("c7 c5"));
        moves.add(parseMove("d4 c5"));
        moves.add(parseMove("d7 d5"));
        moves.add(parseMove("c5 c6"));
        moves.add(parseMove("e7 e5"));
        moves.add(parseMove("c6 b7"));
        moves.add(parseMove("d8 a5"));
        /* Try promotion in check (It does not resolve check, so will be reverted) */
        moves.add(((ChessboardMoveForTest) parseMove("b7 c8").
                setPromotionToWhat(Piece.PieceType.QUEEN)).
                clearIsLegal());
        /* Some illegal moves if reverting was successful */
        moves.add(parseMove("b7 b4").clearIsLegal());
        moves.add(parseMove("a5 c5").clearIsLegal());
        moves.add(parseMove("c8 c3").clearIsLegal());

        /* Blocking check is successful if reverted */
        moves.add(parseMove("c1 d2"));
        runMoves(moves);
    }

    @Test
    void moveILLegal_noEvadeCheck() {
        LinkedList<ChessboardMoveForTest> moves = new LinkedList<>();

        moves.add(parseMove("e2 e4"));
        moves.add(parseMove("f7 f5"));
        moves.add(parseMove("e4 f5"));
        moves.add(parseMove("g7 g6"));
        moves.add(parseMove("f5 g6"));
        moves.add(parseMove("f8 h6"));
        moves.add(parseMove("g6 g7"));
        moves.add(parseMove("g8 f6"));
        moves.add((ChessboardMoveForTest)(parseMove("g7 h8").setPromotionToWhat(Piece.PieceType.QUEEN)));
        moves.add(parseMove("e8 f8").clearIsLegal());
        moves.add(parseMove("h6 f8"));
        runMoves(moves);
    }

    @Test
    void moveLegal_takePieceChecking() {
        LinkedList<ChessboardMoveForTest> moves = new LinkedList<>();

        moves.add(parseMove("e2 e4"));
        moves.add(parseMove("f7 f5"));
        moves.add(parseMove("e4 f5"));
        moves.add(parseMove("g7 g6"));
        moves.add(parseMove("f5 g6"));
        moves.add(parseMove("f8 h6"));
        moves.add(parseMove("g6 g7"));
        moves.add(parseMove("g8 f6"));
        moves.add(parseMove("g1 f3"));
        moves.add(parseMove("h8 f8"));
        moves.add((ChessboardMoveForTest)(parseMove("g7 f8").setPromotionToWhat(Piece.PieceType.QUEEN)));
        moves.add(parseMove("e8 f8"));
        runMoves(moves);
    }

    /* Make itself to be checked by itself */
    @Test
    void moveIllegal_vulnerableItself() {
        LinkedList<ChessboardMoveForTest> moves = new LinkedList<>();

        moves.add(parseMove("e2 e4"));
        moves.add(parseMove("f7 f5"));
        moves.add(parseMove("e4 f5"));
        moves.add(parseMove("g7 g6"));
        moves.add(parseMove("f5 g6"));
        moves.add(parseMove("f8 h6"));
        moves.add(parseMove("g6 g7"));
        moves.add(parseMove("g8 f6"));
        moves.add(parseMove("g1 f3"));
        moves.add(parseMove("h8 f8"));
        moves.add((ChessboardMoveForTest)(parseMove("g7 g8").setPromotionToWhat(Piece.PieceType.ROOK)));
        moves.add(parseMove("f8 f7").clearIsLegal());
        runMoves(moves);
    }

    @Test
    void moveIllegal_promotionNotOnLastRank() {
        LinkedList<ChessboardMoveForTest> moves = new LinkedList<>();

        moves.add((ChessboardMoveForTest)(parseMove("e2 e4").clearIsLegal().setPromotionToWhat(Piece.PieceType.QUEEN)));
        runMoves(moves);
    }

    @Test
    void moveIllegal_promotionFromNotPawn() {
        LinkedList<ChessboardMoveForTest> moves = new LinkedList<>();

        moves.add((ChessboardMoveForTest)(parseMove("g1 f3").clearIsLegal().setPromotionToWhat(Piece.PieceType.QUEEN)));
        runMoves(moves);
    }

    @Test
    void moveLegal_checkIsItCheckmate() {
        LinkedList<ChessboardMoveForTest> moves = new LinkedList<>();

        moves.add(parseMove("e2 e4"));
        moves.add(parseMove("f7 f6"));
        moves.add(parseMove("f1 c4"));
        moves.add(parseMove("g7 g5"));
        moves.add(parseMove("d1 h5"));
        var boardAfterMoves = runMoves(moves);
        Assert.isTrue(boardAfterMoves.isCheckNow(), "IsCheck: False Negative");
        Assert.isTrue(!boardAfterMoves.hasAnyLegalMove(), "hasLegalMove: False Negative");
    }
}
