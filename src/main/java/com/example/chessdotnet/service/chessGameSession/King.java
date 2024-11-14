package com.example.chessdotnet.service.chessGameSession;

import java.util.LinkedList;

public class King extends Piece {
    /**
     * Default constructor
     *
     * @param position Position in chessboard.
     * @param pieceColor Color (maybe WHITE or BLACK) of this.
     * @param chessboard Chessboard where this piece exists.
     */
    public King(ChessboardPos position, Piece.PieceColor pieceColor, Chessboard chessboard) {
        super(position, pieceColor, chessboard);
    }

    /**
     * Return its name(typename of piece)
     *
     * @return Typename of piece
     */
    @Override
    public String toString() {
        return "King";
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

    public boolean hasThreatAtSquare(ChessboardPos pos) {

        ChessboardPos[] directions = {
                new ChessboardPos(1, 1),
                new ChessboardPos(1, -1),
                new ChessboardPos(-1, 1),
                new ChessboardPos(-1, -1),
                new ChessboardPos(1, 0),
                new ChessboardPos(-1, 0),
                new ChessboardPos(0, 1),
                new ChessboardPos(0, -1)
        };

        for (var direction : directions) {
            LinkedList<ChessboardPos> squaresToCheck = new LinkedList<>();
            addSquaresInDirection(squaresToCheck, direction);
            if (squaresToCheck.isEmpty())
                continue;
            var piece = chessboard.getPiece(squaresToCheck.getLast());
            if (piece.isEmptySquare())
                continue;
            if (piece.toString().equals(PieceType.KING.name) && squaresToCheck.size() == 1) {
                return true;
            }
            if (piece.getDestinations().contains(pos))
                return true;
        }

        //noinspection DuplicatedCode
        ChessboardPos[] knightMoves = {
                new ChessboardPos(pos.row + 2, pos.col + 1),
                new ChessboardPos(pos.row + 2, pos.col - 1),
                new ChessboardPos(pos.row - 2, pos.col+ 1),
                new ChessboardPos(pos.row - 2, pos.col - 1),
                new ChessboardPos(pos.row + 1, pos.col + 2),
                new ChessboardPos(pos.row+ 1, pos.col - 2),
                new ChessboardPos(pos.row - 1, pos.col + 2),
                new ChessboardPos(pos.row - 1, pos.col - 2)
        };

        for (var candidate : knightMoves) {
            if (!candidate.isValid())
                continue;

            if (!chessboard.getPiece(candidate).isEmptySquare()
                    && chessboard.getPiece(candidate).pieceColor != this.pieceColor
                    && chessboard.getPiece(candidate).toString().equals(PieceType.KNIGHT.name)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if this king has threat. (determine is it `check`)
     *
     * @return True if this king has threat.
     */
    public boolean checked() {
        return hasThreatAtSquare(this.position);
    }

    /**
     * Get its possible destinations of its legal moves.
     *
     * @return LinkedList of ChessboardPos which contains possible destinations of its legal moves.
     */
    @Override
    public LinkedList<ChessboardPos> getDestinations() {
        LinkedList<ChessboardPos> dests = new LinkedList<>();

        ChessboardPos[] candidates = {
                new ChessboardPos(position.row + 1, position.col + 1),
                new ChessboardPos(position.row + 1, position.col - 1),
                new ChessboardPos(position.row - 1, position.col + 1),
                new ChessboardPos(position.row - 1, position.col - 1),
                new ChessboardPos(position.row + 1, position.col),
                new ChessboardPos(position.row - 1, position.col),
                new ChessboardPos(position.row, position.col + 1),
                new ChessboardPos(position.row, position.col - 1)
        };

        for (var candidatePos : candidates) {
            if (!candidatePos.isValid())
                continue;

            if (hasThreatAtSquare(candidatePos))
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
     * @return true If and only if position updated.
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
