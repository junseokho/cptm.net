package com.example.chessdotnet.service.chessGameSession;

import java.util.LinkedList;

public class Knight extends Piece {
    /**
     * Default constructor
     *
     * @param position Position in chessboard.
     * @param pieceColor Color (maybe WHITE or BLACK) of this.
     * @param chessboard Chessboard where this piece exists.
     */
    public Knight(ChessboardPos position, Piece.PieceColor pieceColor, Chessboard chessboard) {
        super(position, pieceColor, chessboard);
    }

    /**
     * Return its name(typename of piece)
     *
     * @return Typename of piece
     */
    @Override
    public String toString() {
        return "Knight";
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

        //noinspection DuplicatedCode
        ChessboardPos[] candidates = {
                new ChessboardPos(position.row + 2, position.col + 1),
                new ChessboardPos(position.row + 2, position.col - 1),
                new ChessboardPos(position.row - 2, position.col + 1),
                new ChessboardPos(position.row - 2, position.col - 1),
                new ChessboardPos(position.row + 1, position.col + 2),
                new ChessboardPos(position.row + 1, position.col - 2),
                new ChessboardPos(position.row - 1, position.col + 2),
                new ChessboardPos(position.row - 1, position.col - 2)
        };

        for (var candidatePos : candidates) {
            if (!candidatePos.isValid())
                continue;
            /* check it is empty or can be taken (only check if its color is different to this) */
            if (chessboard.getPiece(candidatePos).isEmptySquare()) {
                dests.add(new ChessboardPos(candidatePos));
            } else if (chessboard.getPiece(candidatePos).pieceColor != this.pieceColor) {
                dests.add(new ChessboardPos(candidatePos));
            }
        }

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
        LinkedList<ChessboardPos> possibleDests = getDestinations();
        if (possibleDests.contains(dest)) {
            chessboard.movePiece(this.position, dest);
            return true;
        }

        return false;
    }
}
