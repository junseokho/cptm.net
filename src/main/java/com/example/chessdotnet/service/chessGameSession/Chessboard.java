package com.example.chessdotnet.service.chessGameSession;


import org.springframework.data.util.Pair;
import org.springframework.util.Assert;

import java.util.LinkedList;

/**
 * Chessboard class used by `ChessGameSession`.
 * Its data may only be handled by Pieces. (By `testAndMove()`)
 */
public class Chessboard {
    /**
     * Chessboard of game. 8x8 grid of Pieces.
     */
    protected Piece[][] chessboard;

    /**
     * Piece which moved at latest move.
     * If there's no move in history, it is `null`.
     */
    public Piece nullable_lastMoved;

    /**
     * Piece color which represents that which side has turn now.
     */
    public Piece.PieceColor turnNow;

    /**
     * Records of moves. (History of moves)
     */
    public LinkedList<ChessboardMove> moveRecords;

    /**
     * White's King.
     */
    public King whiteKing;

    /**
     * Black's King.
     */
    public King blackKing;

    /**
     * Get piece in the square, `chessboard[row][col]`.
     * Caller must check if coordinate is valid.
     *
     * @param row Row index of board.
     * @param col Cow index of board.
     * @return Piece in the square at given coordinate.
     */
    public Piece getPiece(int row, int col) {
        Assert.isTrue(ChessboardPos.isValid(row, col), "Chessboard::getPiece() invalid arguments");
        return chessboard[row][col];
    }

    /**
     * Get piece at given coordinate, `pos`
     * Caller must check if coordinate is valid.
     *
     * @param pos coordinate in chessboard
     * @return piece in the square at given coordinate.
     */
    public Piece getPiece(ChessboardPos pos) {
        Assert.isTrue(pos.isValid(), "Chessboard::getPiece() invalid arguments");
        return chessboard[pos.row][pos.col];
    }

    /**
     * Move a piece to dest in given coordinate.
     * It is a safe way to update chessboard, maintaining coherency between chessboard and pieces.
     *
     * @param move Move information.
     */
    public void movePiece(ChessboardMove move) {
        ChessboardPos src = move.startPosition;
        ChessboardPos dest = move.endPosition;

        chessboard[dest.row][dest.col] = chessboard[src.row][src.col];
        chessboard[src.row][src.col] = new Piece(new ChessboardPos(src), Piece.PieceColor.NONE, this);

        chessboard[dest.row][dest.col].position = new ChessboardPos(dest);
        chessboard[dest.row][dest.col].hasMoved = true;

        nullable_lastMoved = chessboard[dest.row][dest.col];

        moveRecords.add(move);

        if (turnNow == Piece.PieceColor.BLACK) {
            turnNow = Piece.PieceColor.WHITE;
        } else {
            turnNow = Piece.PieceColor.BLACK;
        }

        if (!move.isSpecialMove()) return;

        /* There's more need to be done, if it is a special move.
         * If it is castling, need to move Rook.
         * If it is en passant, need to remove the pawn taken by it.
         * If it is promotion, need to replace that pawn into another.
         */

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

            chessboard[newPosOfRook.row][newPosOfRook.col] = chessboard[rowOfRook][colOfRook];
            chessboard[newPosOfRook.row][newPosOfRook.col].position = new ChessboardPos(newPosOfRook.row, newPosOfRook.col);
            chessboard[rowOfRook][colOfRook] = new Piece(
                    new ChessboardPos(rowOfRook, colOfRook),
                    Piece.PieceColor.NONE,
                    this
            );
            chessboard[rowOfRook][colOfRook].hasMoved = true;
        }

        Pair<Boolean, Piece.PieceType> promotionInfo = move.getPromotionInfo();
        if (promotionInfo.getFirst()) {
            Piece newPiece = null;
            switch (promotionInfo.getSecond()) {
                case QUEEN ->
                    newPiece = new Queen(new ChessboardPos(dest), getPiece(dest).pieceColor, this);
                case ROOK ->
                    newPiece = new Rook(new ChessboardPos(dest), getPiece(dest).pieceColor, this);
                case BISHOP ->
                    newPiece = new Bishop(new ChessboardPos(dest), getPiece(dest).pieceColor, this);
                case KNIGHT ->
                    newPiece = new Knight(new ChessboardPos(dest), getPiece(dest).pieceColor, this);
            }
            Assert.isTrue(newPiece != null, "Received promotionInfo is wrong");
            chessboard[dest.row][dest.col] = newPiece;
            chessboard[dest.row][dest.col].hasMoved = true;
            nullable_lastMoved = chessboard[dest.row][dest.col];
        }

        Pair<Boolean, ChessboardPos> enPassantInfo = move.getEnPassantInfo();
        if (enPassantInfo.getFirst()) {
            ChessboardPos posTakenPawn = enPassantInfo.getSecond();
            chessboard[posTakenPawn.row][posTakenPawn.col] = new Piece(
                    new ChessboardPos(posTakenPawn),
                    Piece.PieceColor.NONE,
                    this
            );
        }
    }

    /**
     * Try to move a piece to dest given coordinate.
     * This method is entry point to move a piece, updating Chessboard.
     * IF MOVE IS PROMOTION, NEED TO SET `move` WITH `ChessboardMove::move.setPromotionToWhat()`.
     *
     * @param move Coordinates of squares, start and destination.
     */
    public boolean tryMovePiece(ChessboardMove move) {
        if (!move.startPosition.isValid())
            return false;
        if (!move.endPosition.isValid())
            return false;
        if (getPiece(move.startPosition).pieceColor != turnNow)
            return false;

        var src = move.startPosition;
        var dest = move.endPosition;

        Piece srcPiece = getPiece(src);
        Piece destPiece = getPiece(src);
        var befo_nullable_lastMoved = nullable_lastMoved;
        var kingMaybeChecked = turnNow == Piece.PieceColor.BLACK ?
                blackKing : whiteKing;

        boolean ret;
        if (move.getPromotionInfo().getFirst()) {
            // Promotion is requested but it was not pawn
            if (!getPiece(move.startPosition).toString().equals(Piece.PieceType.PAWN.name)) {
                ret = false;
            } else {
                switch (move.getPromotionInfo().getSecond()) {
                    case QUEEN:
                    case ROOK:
                    case BISHOP:
                    case KNIGHT:
                        ret = ((Pawn) getPiece(move.startPosition)).testAndMovePromotion(move);
                        break;
                    default:
                        ret = false;
                }
            }
        } else
            ret = getPiece(move.startPosition).testAndMove(move.endPosition);

        /* Revert last move if check does been not resolved. */
        if (ret && kingMaybeChecked.checked()) {
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
            return false;
        }
        return ret;
    }

    /**
     * Only check is this move legal by call `Chessboard::tryMovePiece`.
     * After try to move, rollback it.
     *
     * @param move
     * @return
     */
    public boolean tryMoveAndRollback(ChessboardMove move) {
        var src = move.startPosition;
        var dest = move.endPosition;

        Piece srcPiece = getPiece(src);
        Piece destPiece = getPiece(src);
        var befo_nullable_lastMoved = nullable_lastMoved;

        boolean moveRes = tryMovePiece(move);

        if (moveRes) {
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
        return moveRes;
    }

    /**
     * Check is king checked in this turn.
     *
     * @return true if the king of this turn checked, false otherwise.
     */
    public boolean isCheckNow() {
        var kingToTest = turnNow == Piece.PieceColor.BLACK ? blackKing : whiteKing;
        return kingToTest.checked();
    }

    /**
     * Check is there any legal move in this turn.
     *
     * @return true if any legal move exists, false otherwise.
     */
    public boolean hasAnyLegalMove() {
        // for all pieces on board
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                // pieces having color of this turn
                if (!chessboard[r][c].isEmptySquare() && chessboard[r][c].pieceColor == turnNow) {
                    var validDestinations = chessboard[r][c].getDestinations();
                    for (var dest : validDestinations) {
                        ChessboardMove move = new ChessboardMove(new ChessboardPos(r, c), dest);
                        if (chessboard[r][c].toString().equals(Piece.PieceType.PAWN.name) && (r == 7 || r == 0)) {
                            move.setPromotionToWhat(Piece.PieceType.QUEEN);
                        }
                        if (tryMoveAndRollback(move)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Constructor to generate initial state of chessboard
     */
    public Chessboard() {
        final int BOARD_SIZE = 8;
        chessboard = new Piece[BOARD_SIZE][BOARD_SIZE];

        chessboard[0][0] = new Rook(new ChessboardPos(0, 0), Piece.PieceColor.BLACK, this);
        chessboard[0][1] = new Knight(new ChessboardPos(0, 1), Piece.PieceColor.BLACK, this);
        chessboard[0][2] = new Bishop(new ChessboardPos(0, 2), Piece.PieceColor.BLACK, this);
        chessboard[0][3] = new Queen(new ChessboardPos(0, 3), Piece.PieceColor.BLACK, this);
        chessboard[0][4] = blackKing = new King(new ChessboardPos(0, 4), Piece.PieceColor.BLACK, this);
        chessboard[0][5] = new Bishop(new ChessboardPos(0, 5), Piece.PieceColor.BLACK, this);
        chessboard[0][6] = new Knight(new ChessboardPos(0, 6), Piece.PieceColor.BLACK, this);
        chessboard[0][7] = new Rook(new ChessboardPos(0, 7), Piece.PieceColor.BLACK, this);

        for (int i = 0; i < BOARD_SIZE; i++) {
            chessboard[1][i] = new Pawn(new ChessboardPos(1, i), Piece.PieceColor.BLACK, this, new ChessboardPos(1, 0));
        }

        chessboard[7][0] = new Rook(new ChessboardPos(7, 0), Piece.PieceColor.WHITE, this);
        chessboard[7][1] = new Knight(new ChessboardPos(7, 1), Piece.PieceColor.WHITE, this);
        chessboard[7][2] = new Bishop(new ChessboardPos(7, 2), Piece.PieceColor.WHITE, this);
        chessboard[7][3] = new Queen(new ChessboardPos(7, 3), Piece.PieceColor.WHITE, this);
        chessboard[7][4] = whiteKing = new King(new ChessboardPos(7, 4), Piece.PieceColor.WHITE, this);
        chessboard[7][5] = new Bishop(new ChessboardPos(7, 5), Piece.PieceColor.WHITE, this);
        chessboard[7][6] = new Knight(new ChessboardPos(7, 6), Piece.PieceColor.WHITE, this);
        chessboard[7][7] = new Rook(new ChessboardPos(7, 7), Piece.PieceColor.WHITE, this);

        for (int i = 0; i < BOARD_SIZE; i++) {
            chessboard[6][i] = new Pawn(new ChessboardPos(6, i), Piece.PieceColor.WHITE, this, new ChessboardPos(-1, 0));
        }

        for (int row = 2; row <= 5; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                chessboard[row][col] = new Piece(new ChessboardPos(row, col), Piece.PieceColor.NONE, this);
            }
        }

        nullable_lastMoved = null;
        turnNow = Piece.PieceColor.WHITE;
        moveRecords = new LinkedList<>();
    }
}

//public boolean tryMoveAndRollback(ChessboardMove move) {
//    var src = move.startPosition;
//    var dest = move.endPosition;
//
//    Piece srcPiece = getPiece(src);
//    Piece destPiece = getPiece(src);
//    var befo_nullable_lastMoved = nullable_lastMoved;
//
//    var ret = tryMovePiece(src, dest);
//    if (ret) {
//        nullable_lastMoved = befo_nullable_lastMoved;
//        chessboard[src.row][src.col] = srcPiece;
//        chessboard[dest.row][dest.col] = destPiece;
//
//        if (turnNow == Piece.PieceColor.BLACK) {
//            turnNow = Piece.PieceColor.WHITE;
//        } else {
//            turnNow = Piece.PieceColor.BLACK;
//        }
//
//        moveRecords.removeLast();
//
//        if (move.isCastling()) {
//            ChessboardPos castleDirection = ChessboardPos.sub(dest, src);
//            int colOfRook;
//            if (castleDirection.col > 0) {
//                colOfRook = 7;
//            } else {
//                colOfRook = 0;
//            }
//
//            int rowOfRook = dest.row;
//
//            ChessboardPos newPosOfRook = new ChessboardPos(
//                    rowOfRook,
//                    castleDirection.col > 0 ? dest.col - 1 : dest.col + 1
//            );
//
//            chessboard[rowOfRook][colOfRook] = chessboard[newPosOfRook.row][newPosOfRook.col];
//            chessboard[rowOfRook][colOfRook].position = new ChessboardPos(rowOfRook, colOfRook);
//            chessboard[newPosOfRook.row][newPosOfRook.col] = new Piece(
//                    new ChessboardPos(rowOfRook, colOfRook),
//                    Piece.PieceColor.NONE,
//                    this
//            );
//        }
//
//        if (move.getEnPassantInfo().getFirst()) {
//            ChessboardPos pawnTaken = move.getEnPassantInfo().getSecond();
//            chessboard[pawnTaken.row][pawnTaken.col] = new Pawn(
//                    new ChessboardPos(pawnTaken),
//                    (turnNow == Piece.PieceColor.BLACK) ? Piece.PieceColor.WHITE : Piece.PieceColor.BLACK,
//                    this,
//                    (turnNow == Piece.PieceColor.BLACK) ? new ChessboardPos(1, 0) : new ChessboardPos(-1, 0)
//            );
//        }
//    }
//    return ret;
//}