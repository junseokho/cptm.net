package com.example.chessdotnet.service.chessGameSession;

import java.util.LinkedList;


/**
 * Bishop class of chess piece
 */
public class Bishop extends Piece {
    /**
     * Default constructor
     *
     * @param position Position in chessboard.
     * @param pieceColor Color (maybe WHITE or BLACK) of this.
     * @param chessboard Chessboard where this piece exists.
     */
    public Bishop(ChessboardPos position, PieceColor pieceColor, Chessboard chessboard) {
        super(position, pieceColor, chessboard);
    }

    /**
     * Return its name(typename of piece)
     *
     * @return Typename of piece
     */
    @Override
    public String toString() {
        return "Bishop";
    }

    /**
     * Check is it Empty Square (Not a Piece)
     * Must be overridden
     *
     * @return True if it is empty square
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
     * Update its position and chessboard, if dest is reachable in one move by itself.
     * It is a safe way to update chessboard, maintaining coherency between `piece.position`
     * and actual position in chessboard.
     *
     * @param dest Destination of move to test
     * @return True if and only if position updated.
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
