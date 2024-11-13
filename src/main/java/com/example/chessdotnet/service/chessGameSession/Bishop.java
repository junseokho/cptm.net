package com.example.chessdotnet.service.chessGameSession;

import java.util.LinkedList;


/**
 * Bishop class of chess piece
 */
public class Bishop extends Piece {
    /**
     * default constructor
     *
     * @param position position in chessboard.
     * @param pieceColor color (maybe WHITE or BLACK) of this.
     * @param chessboard chessboard where this piece exists.
     */
    public Bishop(ChessboardPos position, PieceColor pieceColor, Chessboard chessboard) {
        super(position, pieceColor, chessboard);
    }

    /**
     * return its name(typename of piece)
     *
     * @return typename of piece
     */
    @Override
    public String toString() {
        return "Bishop";
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

        addSquaresInDirection(dests, new ChessboardPos(1, 1));
        addSquaresInDirection(dests, new ChessboardPos(1, -1));
        addSquaresInDirection(dests, new ChessboardPos(-1, 1));
        addSquaresInDirection(dests, new ChessboardPos(-1, -1));

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
        //noinspection DuplicatedCode
        ChessboardPos diff = ChessboardPos.sub(this.position, dest);
        /* `diff.row == 0` is needed to check dest equals to this.position */
        if (Math.abs(diff.row) != Math.abs(diff.col) || diff.row == 0)
            return false;

        /* make them into 1 or -1 */
        diff.row = diff.row / Math.abs(diff.row);
        diff.col = diff.col / Math.abs(diff.col);

        if (checkPosInDirection(dest, diff)) {
            chessboard.movePiece(this.position, dest);
            return true;
        }

        return false;
    }
}
