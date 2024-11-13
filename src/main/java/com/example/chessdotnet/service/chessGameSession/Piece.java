package com.example.chessdotnet.service.chessGameSession;

import java.util.LinkedList;


/**
 * class, extended by Actual Pieces (Rook, Bishop, ..., etc.)
 * Itself, it means empty square in chessboard. (I know that's such an Anti-pattern)
 */
class Piece {
    /**
     * Manage same values for pieces' color (White or Black)
     */
    public enum PieceColor {
        WHITE (0, "white"),
        BLACK (1, "black"),
        NONE (2, "none");

        final public int idx;
        final public String name;

        PieceColor(int idx, String name) {
            this.idx = idx;
            this.name = name;
        }
    }

    /**
     * Manage same names for pieces' type
     */
    public enum PieceType {
        KING ("King", "K"),
        QUEEN ("Queen", "Q"),
        ROOK ("Rook", "R"),
        BISHOP ("Bishop", "B"),
        KNIGHT ("Knight", "N"),
        PAWN ("Pawn", "P");


        final public String name;
        final public String initial;

        PieceType(String name, String initial) {
            this.initial = initial;
            this.name = name;
        }
    }

    /**
     * position in chessboard of this
     */
    public ChessboardPos position;

    /**
     * color (maybe WHITE or BLACK) of this
     */
    public PieceColor pieceColor;

    /**
     * true if it moves at least once.
     */
    public boolean hasMoved;

    /**
     * chessboard which this piece belongs to.
     */
    public Chessboard chessboard;

    /**
     * default constructor
     *
     * @param position position in chessboard.
     * @param pieceColor color (maybe WHITE or BLACK) of this.
     * @param chessboard chessboard where this piece exists.
     */
    public Piece(ChessboardPos position, PieceColor pieceColor, Chessboard chessboard) {
        this.position = position;
        this.pieceColor = pieceColor;
        this.hasMoved = false;
        this.chessboard = chessboard;
    }


    /**
     * return its name(typename of piece)
     *
     * @return typename of piece
     */
    public String toString() {
        return "Piece";
    }

    /**
     * check is it Empty Square (Not a Piece)
     * Must be overridden
     *
     * @return true if it is empty square
     */
    public boolean isEmptySquare() {
        return true;
    }

    /**
     * Get its possible destinations of its legal moves.
     *
     * @return LinkedList of ChessboardPos which contains possible destinations of its legal moves.
     */
    public LinkedList<ChessboardPos> getDestinations() {
        return new LinkedList<>();
    }

    /**
     * update its position and chessboard, if dest is reachable in one move by itself.
     *
     * @param dest destination of move to test
     * @return true if and only if position updated.
     */
    public boolean testAndMove(ChessboardPos dest) {
        return false;
    }

    /**
     * Check given position can be obtained by adding stride on `this.position` repeatedly while
     * position is valid. "valid" means not blocked by same colors and also valid position.
     * Don't be confused, it **does not** add values onto `this.position` in directly.
     * Note that, this method is not designed for pieces which can jump over another piece.
     *
     * @param pos position of chessboard to check
     * @param stride direction added on `this.position` repeatedly.
     * @return true if given position can be obtained by adding stride on `this.position` repeatedly.
     */
    public boolean checkPosInDirection(ChessboardPos pos, ChessboardPos stride) {
        ChessboardPos candidatePos = new ChessboardPos(position);

        while (candidatePos
                .add(stride)
                .isValid()) {

            /* check it is empty or can be taken (only check if its color is different to this) */
            if (chessboard.getPiece(candidatePos).isEmptySquare()) {
                if (candidatePos.equals(pos)) return true;
            } else if (chessboard.getPiece(candidatePos).pieceColor != this.pieceColor) {
                if (candidatePos.equals(pos)) return true;
            } else {
                /* from this position, blocked by the same colors */
                break;
            }
        }
        return false;
    }

    /**
     * Insert ChessboardPoses into out, which can be obtained by adding stride on `this.position` repeatedly while
     * position is valid. "valid" means not blocked by same colors and also valid position.
     * Don't be confused, it **does not** add values onto `this.position` in directly.
     * Note that, this method is not designed for pieces which can jump over another piece.
     *
     * @param out container to collect possible
     * @param stride direction added on `this.position` repeatedly.
     */
    public void addSquaresInDirection(LinkedList<ChessboardPos> out, ChessboardPos stride) {
        ChessboardPos candidatePos = new ChessboardPos(position);

        while (candidatePos
                .add(stride)
                .isValid()) {

            /* check it is empty or can be taken (only check if its color is different to this) */
            if (chessboard.getPiece(candidatePos).isEmptySquare()) {
                out.add(new ChessboardPos(candidatePos));
            } else if (chessboard.getPiece(candidatePos).pieceColor != this.pieceColor) {
                out.add(new ChessboardPos(candidatePos));
            } else {
                /* from this position, blocked by the same colors */
                break;
            }
        }
    }
}
