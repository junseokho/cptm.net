import React, { useState } from 'react';
import ChessBoard from './ChessBoard.jsx';
import { Rook, Knight, Bishop, Queen, King, Pawn } from './Piece.jsx';

const initialBoardSetup = [
    [
        new Rook('Rook', { x: 0, y: 0 }, 'white', '../assets/wR.svg'),
        new Knight('Knight', { x: 1, y: 0 }, 'white', '../assets/wN.svg'),
        new Bishop('Bishop', { x: 2, y: 0 }, 'white', '../assets/wB.svg'),
        new Queen('Queen', { x: 3, y: 0 }, 'white', '../assets/wQ.svg'),
        new King('King', { x: 4, y: 0 }, 'white', '../assets/wK.svg'),
        new Bishop('Bishop', { x: 5, y: 0 }, 'white', '../assets/wB.svg'),
        new Knight('Knight', { x: 6, y: 0 }, 'white', '../assets/wN.svg'),
        new Rook('Rook', { x: 7, y: 0 }, 'white', '../assets/wR.svg'),
    ],
    Array(8).fill(null).map((_, idx) => new Pawn('Pawn', { x: idx, y: 1 }, 'white', '/images/white-pawn.png')), // White pawns
    Array(8).fill(null), // Empty row
    Array(8).fill(null), // Empty row
    Array(8).fill(null), // Empty row
    Array(8).fill(null), // Empty row
    Array(8).fill(null).map((_, idx) => new Pawn('Pawn', { x: idx, y: 6 }, 'black', '/images/black-pawn.png')), // Black pawns
    [
        new Rook('Rook', { x: 0, y: 7 }, 'black', '../assets/bR.svg'),
        new Knight('Knight', { x: 1, y: 7 }, 'black', '../assets/bN.svg'),
        new Bishop('Bishop', { x: 2, y: 7 }, 'black', '../assets/bB.svg'),
        new Queen('Queen', { x: 3, y: 7 }, 'black', '../assets/bQ.svg'),
        new King('King', { x: 4, y: 7 }, 'black', '../assets/bK.svg'),
        new Bishop('Bishop', { x: 5, y: 7 }, 'black', '../assets/bB.svg'),
        new Knight('Knight', { x: 6, y: 7 }, 'black', '../assets/bN.svg'),
        new Rook('Rook', { x: 7, y: 7 }, 'black', '../assets/bR.svg'),
    ]
];

const ChessBoardLogic = () => {
    const [board] = useState(initialBoardSetup); // setBoard 제거

    const handlePieceClick = (piece) => { // row, col 제거
        if (piece) {
            const availableMoves = piece.getAvailableMoves(board);
            highlightMoves(availableMoves);
        }
    };

    const highlightMoves = (moves) => {
        moves.forEach((move) => {
            const cell = document.querySelector(`[data-position="${move.x}-${move.y}"]`);
            if (cell) {
                cell.classList.add('highlight');
            }
        });
    };

    return (
        <ChessBoard board={board} onPieceClick={handlePieceClick} />
    );
};

export default ChessBoardLogic; // 디폴트 내보내기 유지