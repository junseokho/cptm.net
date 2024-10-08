import React, { useState } from 'react';
import ChessBoard from './ChessBoard.jsx';
import { Rook, Knight, Bishop, Queen, King, Pawn } from './Piece.jsx';

const initialBoardSetup = [
    [
        new Rook('Rook', { x: 0, y: 0 }, 'black', '/src/assets/bR.svg'),
        new Knight('Knight', { x: 1, y: 0 }, 'black', '/src/assets/bN.svg'),
        new Bishop('Bishop', { x: 2, y: 0 }, 'black', '/src/assets/bB.svg'),
        new Queen('Queen', { x: 3, y: 0 }, 'black', '/src/assets/bQ.svg'),
        new King('King', { x: 4, y: 0 }, 'black', '/src/assets/bK.svg'),
        new Bishop('Bishop', { x: 5, y: 0 }, 'black', '/src/assets/bB.svg'),
        new Knight('Knight', { x: 6, y: 0 }, 'black', '/src/assets/bN.svg'),
        new Rook('Rook', { x: 7, y: 0 }, 'black', '/src/assets/bR.svg'),
    ],
    Array(8).fill(null).map((_, idx) => new Pawn('Pawn', { x: idx, y: 1 }, 'black', '/src/assets/bP.svg')),
    Array(8).fill(null),
    Array(8).fill(null),
    Array(8).fill(null),
    Array(8).fill(null),
    Array(8).fill(null).map((_, idx) => new Pawn('Pawn', { x: idx, y: 6 }, 'white', '/src/assets/wP.svg')),
    [
        new Rook('Rook', { x: 0, y: 7 }, 'white', '/src/assets/wR.svg'),
        new Knight('Knight', { x: 1, y: 7 }, 'white', '/src/assets/wN.svg'),
        new Bishop('Bishop', { x: 2, y: 7 }, 'white', '/src/assets/wB.svg'),
        new Queen('Queen', { x: 3, y: 7 }, 'white', '/src/assets/wQ.svg'),
        new King('King', { x: 4, y: 7 }, 'white', '/src/assets/wK.svg'),
        new Bishop('Bishop', { x: 5, y: 7 }, 'white', '/src/assets/wB.svg'),
        new Knight('Knight', { x: 6, y: 7 }, 'white', '/src/assets/wN.svg'),
        new Rook('Rook', { x: 7, y: 7 }, 'white', '/src/assets/wR.svg'),
    ]
];

const ChessBoardLogic = () => {
    const [board] = useState(initialBoardSetup);
    const [highlightedSquares, setHighlightedSquares] = useState([]);
    const [selectedSquare, setSelectedSquare] = useState(null);

    const handlePieceClick = (piece, position) => {
        // 선택 상태 초기화
        setHighlightedSquares([]);
        setSelectedSquare(null);

        if (piece) {
            const availableMoves = piece.getAvailableMoves(board);
            console.log(`Selected piece: ${piece.name}`);
            console.log('Available moves:', availableMoves);

            // 이동 가능한 칸 상태 업데이트
            setHighlightedSquares(availableMoves);
            setSelectedSquare(position);
        }
    };

    return (
        <ChessBoard
            board={board}
            onPieceClick={handlePieceClick}
            highlightedSquares={highlightedSquares}
            selectedSquare={selectedSquare}
        />
    );
};

export default ChessBoardLogic;
