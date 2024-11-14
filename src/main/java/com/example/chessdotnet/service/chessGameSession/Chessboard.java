package com.example.chessdotnet.service.chessGameSession;


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
    private Piece[][] chessboard;

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
     * @param src A coordinate of a piece which wants to move
     * @param dest A coordinate of a destination
     */
    public void movePiece(ChessboardPos src, ChessboardPos dest) {
        chessboard[dest.row][dest.col] = chessboard[src.row][src.col];
        chessboard[src.row][src.col] = new Piece(new ChessboardPos(src), Piece.PieceColor.NONE, this);

        chessboard[dest.row][dest.col].position = new ChessboardPos(dest);
        chessboard[dest.row][dest.col].hasMoved = true;

        nullable_lastMoved = chessboard[dest.row][dest.col];

        moveRecords.add(new ChessboardMove(new ChessboardPos(src), new ChessboardPos(dest)));

        if (turnNow == Piece.PieceColor.BLACK) {
            turnNow = Piece.PieceColor.WHITE;
        } else {
            turnNow = Piece.PieceColor.BLACK;
        }
    }

    /**
     * Try to move a piece to dest given coordinate.
     * This method is entry point to move a piece, updating Chessboard.
     *
     * @param src A coordinate of a piece which wants to move
     * @param dest A coordinate of a destination
     */
    public boolean tryMovePiece(ChessboardPos src, ChessboardPos dest) {
        if (!src.isValid())
            return false;
        if (!dest.isValid())
            return false;
        if (getPiece(src).pieceColor != turnNow)
            return false;
        return getPiece(src).testAndMove(dest);
    }

    /**
     * Try to move a piece to dest given coordinate.
     * This method is entry point to move a piece, updating Chessboard.
     *
     * @param move Coordinates of squares, start and destination.
     */
    public boolean tryMovePiece(ChessboardMove move) {
        return getPiece(move.startPosition).testAndMove(move.endPosition);
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
        chessboard[0][4] = new King(new ChessboardPos(0, 4), Piece.PieceColor.BLACK, this);
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
        chessboard[7][4] = new King(new ChessboardPos(7, 4), Piece.PieceColor.WHITE, this);
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
