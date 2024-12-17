package com.example.chessdotnet.service.chessGameSession;

import org.springframework.data.util.Pair;

/**
 * Class to record moves
 * Must be set after checking is move valid, because this class doesn't care if it is valid.
 */
public class ChessboardMove {
    /**
     * Moved piece's position before move
     */
    public ChessboardPos startPosition;

    /**
     * Moved piece's position after move
     */
    public ChessboardPos endPosition;

    /**
     * Type of the moved piece after promoted.
     */
    private Piece.PieceType promotionToWhat;

    /**
     * Coordinate of piece taken by the move, en passant(=this move).
     */
    private ChessboardPos posTakenByEnPassant;

    /**
     * True when this move is castling. (in any way)
     */
    private boolean isCastling;

    /**
     * If `promotionToWhat`, `posTakenByEnPassant`, or `isCastling` is set, this is `true`.
     */
    private boolean isSpecialMove;


    /**
     * Get Information of promotionToWhat. It can be null, if this move is not a promotion.
     *
     * @return First element of Pair represents is it not null, the second is actual information.
     * If the first is `false`, the second is meaningless.
     */
    public Pair<Boolean, Piece.PieceType> getPromotionInfo() {
        if (promotionToWhat == null) {
            return Pair.of(false, Piece.PieceType.KING);
        } else {
            return Pair.of(true, promotionToWhat);
        }
    }

    /**
     * Get Information of posTakenByEnPassant. It can be null, if this move is not an en passant.
     *
     * @return First element of Pair represents is it not null, the second is actual information.
     * If the first is `false`, the second is meaningless.
     */
    public Pair<Boolean, ChessboardPos> getEnPassantInfo() {
        if (posTakenByEnPassant == null) {
            return Pair.of(false, new ChessboardPos(-1, -1));
        } else {
            return Pair.of(true, posTakenByEnPassant);
        }
    }

    /**
     * Get if it is a castling.
     *
     * @return True if this move is castling.
     */
    public boolean isCastling() {
        return isCastling;
    }

    /**
     * Get if `promotionToWhat`, `posTakenByEnPassant`, or `isCastling` is set.
     *
     * @return True if one of them is set.
     */
    public boolean isSpecialMove() {
        return isSpecialMove;
    }

    /**
     * Set the promotion info.
     *
     * @param pieceType Type of the moved piece after promoted.
     * @return Return itself
     */
    public ChessboardMove setPromotionToWhat(Piece.PieceType pieceType) {
        promotionToWhat = pieceType;
        isSpecialMove = true;
        return this;
    }

    /**
     * Set the en passant info, with `posTakenByEnPassant`.
     *
     * @param posTakenByEnPassant Coordinate of the pawn, which is taken by this en passant.
     * @return Return itself
     */
    public ChessboardMove setEnPassant(ChessboardPos posTakenByEnPassant) {
        this.posTakenByEnPassant = posTakenByEnPassant;
        isSpecialMove = true;
        return this;
    }

    /**
     * Set isCastling to true.
     * @return Return itself
     */
    public ChessboardMove setCastling() {
        isCastling = true;
        isSpecialMove = true;
        return this;
    }



    /**
     * Default constructor.
     *
     * @param src Coordinate of the moved piece before moving.
     * @param dest Coordinate of the moved piece after moving.
     */
    public ChessboardMove(ChessboardPos src, ChessboardPos dest) {
        startPosition = src;
        endPosition = dest;

        promotionToWhat = null;
        posTakenByEnPassant = null;
        isCastling = false;
        isSpecialMove = false;
    }
}