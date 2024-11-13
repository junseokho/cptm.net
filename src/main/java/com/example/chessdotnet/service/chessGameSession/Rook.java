package com.example.chessdotnet.service.chessGameSession;

import java.util.LinkedList;


/**
 * Rook class of chess piece
 */
public class Rook extends Piece {
    /**
     * default constructor
     *
     * @param position position in chessboard.
     * @param pieceColor color (maybe WHITE or BLACK) of this.
     * @param chessboard chessboard where this piece exists.
     */
    public Rook(ChessboardPos position, PieceColor pieceColor, Chessboard chessboard) {
        super(position, pieceColor, chessboard);
    }

    /**
     * return its name(typename of piece)
     *
     * @return typename of piece
     */
    @Override
    public String toString() {
        return "Rook";
    }

    /**
     * check is it Empty Square (Not a Piece)
     * Must be overridden
     *
     * @return true if it is empty square
     */
    @Override
    public boolean isEmptySquare() {
        return false;
    }

    /**
     * Get its possible destinations of its legal moves.
     *
     * @return LinkedList of ChessboardPos which contains possible destinations of its legal moves.
     */
    @Override
    public LinkedList<ChessboardPos> getDestinations() {
        LinkedList<ChessboardPos> dests = new LinkedList<>();

        addSquaresInDirection(dests, new ChessboardPos(0, 1));
        addSquaresInDirection(dests, new ChessboardPos(0, -1));
        addSquaresInDirection(dests, new ChessboardPos(-1, 0));
        addSquaresInDirection(dests, new ChessboardPos(-1, 0));

        return dests;
    }

    /**
     * update its position and chessboard, if dest is reachable in one move by itself.
     * It is a safe way to update chessboard, maintaining coherency between `piece.position`
     * and actual position in chessboard.
     *
     * @param dest destination of move to test
     * @return true if and only if position updated.
     */
    @Override
    public boolean testAndMove(ChessboardPos dest) {
        /* check if them have same row or col, but not both (XNOR) */
        if ((dest.row == position.row) == (dest.col == position.col))
            return false;

        ChessboardPos diff = ChessboardPos.sub(dest, position);

        /* get direction, src to dest */
        if (diff.row != 0) diff.row /= Math.abs(diff.row);
        if (diff.col != 0) diff.col /= Math.abs(diff.col);

        if (checkPosInDirection(dest, diff)) {
            chessboard.movePiece(this.position, dest);
            return true;
        }

        return false;
    }
}
