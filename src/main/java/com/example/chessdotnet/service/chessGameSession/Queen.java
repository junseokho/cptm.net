package com.example.chessdotnet.service.chessGameSession;

import java.util.LinkedList;


/**
 * Bishop class of chess piece
 */
public class Queen extends Piece {
    /**
     * Default constructor
     *
     * @param position Position in chessboard.
     * @param pieceColor Color (maybe WHITE or BLACK) of this.
     * @param chessboard Chessboard where this piece exists.
     */
    public Queen(ChessboardPos position, PieceColor pieceColor, Chessboard chessboard) {
        super(position, pieceColor, chessboard);
    }

    /**
     * Return its name(typename of piece)
     *
     * @return Typename of piece
     */
    @Override
    public String toString() {
        return "Queen";
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

        addSquaresInDirection(dests, new ChessboardPos(0, 1));
        addSquaresInDirection(dests, new ChessboardPos(0, -1));
        addSquaresInDirection(dests, new ChessboardPos(-1, 0));
        addSquaresInDirection(dests, new ChessboardPos(-1, 0));

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
        ChessboardPos diff = ChessboardPos.sub(dest, position);

        /* `diff.row == 0` is needed to check dest equals to this.position */
        if (Math.abs(diff.row) == Math.abs(diff.col) && diff.row != 0) {
            /* make them into 1 or -1 */
            diff.row = diff.row / Math.abs(diff.row);
            diff.col = diff.col / Math.abs(diff.col);

            if (checkPosInDirection(dest, diff)) {
                chessboard.movePiece(new ChessboardMove(
                        new ChessboardPos(this.position),
                        new ChessboardPos(dest)
                ));
                return true;
            }
            /* check if them have same row or col, but not both (XNOR) */
        } else if ((dest.row == position.row) != (dest.col == position.col)) {

            /* get direction, src to dest */
            if (diff.row != 0) diff.row /= Math.abs(diff.row);
            if (diff.col != 0) diff.col /= Math.abs(diff.col);

            if (checkPosInDirection(dest, diff)) {
                chessboard.movePiece(new ChessboardMove(
                        new ChessboardPos(this.position),
                        new ChessboardPos(dest)
                ));
                return true;
            }
        }
        return false;
    }
}
