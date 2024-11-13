package com.example.chessdotnet.service.chessGameSession;

import lombok.Getter;
import lombok.Setter;

/**
 * class to record moves
 * Must be set after checking is move valid, because this class doesn't care if it is valid.
 */
@Getter
@Setter
public class ChessboardMove {
    /**
     * moved piece's position before move
     */
    public ChessboardPos startPosition;

    /**
     * moved piece's position after move
     */
    public ChessboardPos endPosition;

    public ChessboardMove(ChessboardPos src, ChessboardPos dest) {
        startPosition = src;
        endPosition = dest;
    }
}