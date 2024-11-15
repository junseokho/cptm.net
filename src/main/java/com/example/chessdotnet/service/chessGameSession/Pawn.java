package com.example.chessdotnet.service.chessGameSession;

import org.springframework.util.Assert;

import java.util.LinkedList;


/**
 * Pawn class of chess piece
 */
public class Pawn extends Piece {
    /**
     * Direction of forward of this pawn. (At now, this class supports only case that `directionForward.col` is zero.)
     */
    public ChessboardPos directionForward;

    /**
     * Default constructor
     *
     * @param position Position in chessboard.
     * @param pieceColor Color (maybe WHITE or BLACK) of this.
     * @param chessboard Chessboard where this piece exists.
     * @param directionForward Stride of this pawn in forward.
     */
    public Pawn(ChessboardPos position, PieceColor pieceColor, Chessboard chessboard, ChessboardPos directionForward) {
        super(position, pieceColor, chessboard);
        this.directionForward = directionForward;
    }

    /**
     * Return its name(typename of piece)
     *
     * @return Typename of piece
     */
    @Override
    public String toString() {
        return "Pawn";
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
     * Implementing this logic was ........................................................
     *
     * @return LinkedList of ChessboardPos which contains possible destinations of its legal moves.
     */
    @Override
    public LinkedList<ChessboardPos> getDestinations() {
        LinkedList<ChessboardPos> dests = new LinkedList<>();

        ChessboardPos leftDiagonal = new ChessboardPos(position.row + directionForward.row, position.col - 1);
        ChessboardPos rightDiagonal = new ChessboardPos(position.row + directionForward.row, position.col + 1);
        ChessboardPos forward = new ChessboardPos(position.row + directionForward.row, position.col);

        ChessboardPos[] diagonals = { leftDiagonal, rightDiagonal };
        for (var diagonal : diagonals) {
            if (diagonal.isValid()
                    && !chessboard.getPiece(diagonal).isEmptySquare()
                    && chessboard.getPiece(diagonal).pieceColor != this.pieceColor) {
                dests.add(new ChessboardPos(diagonal));
                continue;
            }

            /* step to check en passant */
            int direction = diagonal.col - position.col;
            if (!ChessboardPos.isValid(position.row, position.col + direction))
                continue;

            if (!chessboard
                    .getPiece(position.row, position.col + direction)
                    .toString()
                    .equals("Pawn"))
                continue;

            if (chessboard.moveRecords.isEmpty())
                continue;

            if (chessboard.nullable_lastMoved != chessboard.getPiece(position.row, position.col + direction))
                continue;

            var lastMove = chessboard.moveRecords.getLast();
            var strideLastMove = ChessboardPos.sub(lastMove.endPosition, lastMove.startPosition);

            if (!((Math.abs(strideLastMove.row) == 2 && strideLastMove.col == 0)
                    || (strideLastMove.row == 0 && Math.abs(strideLastMove.col) == 2)))
                continue;

            dests.add(new ChessboardPos(diagonal));
        }

        if (forward.isValid() && chessboard.getPiece(forward).isEmptySquare()) {
            dests.add(new ChessboardPos(forward));
            forward.add(directionForward);
            /* check if pawn is able to move two steps */
            if (!hasMoved && forward.isValid() && chessboard.getPiece(forward).isEmptySquare())
                dests.add(new ChessboardPos(forward));
        }

        return dests;
    }

    /**
     * Only check that destination of move is legal.
     *
     * @param dest Destination of a move.
     * @return True if that destination is legal.
     */
    public boolean isLegalMove(ChessboardPos dest) {
        LinkedList<ChessboardPos> possibleDests = getDestinations();
        return possibleDests.contains(dest);
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
        if (isLegalMove(dest)) {
            ChessboardMove move = new ChessboardMove(new ChessboardPos(position), new ChessboardPos(dest));
            /* If it is en passant */
            if (dest.col != position.col && chessboard.getPiece(dest).isEmptySquare()) {
                move.setEnPassant(new ChessboardPos(position.row, dest.col));
            }

            /* Promotion is handled by `Pawn::testAndMovePromotion()` */
            chessboard.movePiece(move);
            return true;
        }

        return false;
    }

    /**
     * If the move is promotion, you need to call this.
     *
     * @param move ChessboardMove instance built by caller.
     * @return True if and only if position updated.
     */
    public boolean testAndMovePromotion(ChessboardMove move) {
        Assert.isTrue(move.getPromotionInfo().getFirst(), "Need to have promotion info");
        if (isLegalMove(move.endPosition)) {
            chessboard.movePiece(move);
            return true;
        }
        return false;
    }
}
