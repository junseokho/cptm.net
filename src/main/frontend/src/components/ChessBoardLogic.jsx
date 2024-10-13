import React, { useState } from 'react';
import ChessBoard from './ChessBoard.jsx';
import { Rook, Knight, Bishop, Queen, King, Pawn } from './Piece.jsx';

/**
 * 초기 체스판 설정을 정의한 배열
 * @type {Array<Array<Piece|null>>}
 */
const initialBoardSetup = [
    // white pieces
    [
        new Rook('Rook', { row: 0, col: 0 }, 'white', '/src/assets/wR.svg'),
        new Knight('Knight', { row: 1, col: 0 }, 'white', '/src/assets/wN.svg'),
        new Bishop('Bishop', { row: 2, col: 0 }, 'white', '/src/assets/wB.svg'),
        new Queen('Queen', { row: 3, col: 0 }, 'white', '/src/assets/wQ.svg'),
        new King('King', { row: 4, col: 0 }, 'white', '/src/assets/wK.svg'),
        new Bishop('Bishop', { row: 5, col: 0 }, 'white', '/src/assets/wB.svg'),
        new Knight('Knight', { row: 6, col: 0 }, 'white', '/src/assets/wN.svg'),
        new Rook('Rook', { row: 7, col: 0 }, 'white', '/src/assets/wR.svg'),
    ],
    // white pawns
    Array(8).fill(null).map((_, idx) => new Pawn('Pawn', { row: idx, col: 1 }, 'white', '/src/assets/wP.svg')),
    // empty squares
    Array(8).fill(null),
    Array(8).fill(null),
    Array(8).fill(null),
    Array(8).fill(null),
    // black pawns
    Array(8).fill(null).map((_, idx) => new Pawn('Pawn', { row: idx, col: 6 }, 'black', '/src/assets/bP.svg')),
    // black pieces
    [
        new Rook('Rook', { row: 0, col: 7 }, 'black', '/src/assets/bR.svg'),
        new Knight('Knight', { row: 1, col: 7 }, 'black', '/src/assets/bN.svg'),
        new Bishop('Bishop', { row: 2, col: 7 }, 'black', '/src/assets/bB.svg'),
        new Queen('Queen', { row: 3, col: 7 }, 'black', '/src/assets/bQ.svg'),
        new King('King', { row: 4, col: 7 }, 'black', '/src/assets/bK.svg'),
        new Bishop('Bishop', { row: 5, col: 7 }, 'black', '/src/assets/bB.svg'),
        new Knight('Knight', { row: 6, col: 7 }, 'black', '/src/assets/bN.svg'),
        new Rook('Rook', { row: 7, col: 7 }, 'black', '/src/assets/bR.svg'),
    ]
];

/**
 * ChessBoardLogic 컴포넌트 - 체스판 로직을 제어하는 컴포넌트
 *
 * @component
 * @returns {JSX.Element} - 체스판 로직 및 렌더링을 포함한 JSX 요소
 */
const ChessBoardLogic = () => {
    const [board] = useState(initialBoardSetup);
    const [highlightedSquares, setHighlightedSquares] = useState([]);
    const [selectedSquare, setSelectedSquare] = useState(null);
    const [selectedPiece, setSelectedPiece] = useState(null);

    /**
     * 기물이 클릭되었을 때 호출되는 함수
     *
     * @param {Object} piece - 클릭된 기물 객체
     * @param {Object} position - 클릭된 기물의 위치 {row: number, col: number}
     */
    const handlePieceClick = (piece, position) => {
        setHighlightedSquares([]);
        setSelectedSquare(null);
        setSelectedPiece(null);

        if (piece) {
            const availableMoves = piece.getAvailableMoves(board);
            console.log(`Selected piece: ${piece.name}`);
            console.log('Available moves:', availableMoves);

            setHighlightedSquares(availableMoves);
            setSelectedSquare(position);
            setSelectedPiece(piece);
        }
    };

    /**
     * 빈 칸이 클릭되었을 때 호출되는 함수
     *
     * @param {Object} targetPosition - 클릭된 빈 칸의 위치 {row: number, col: number}
     */
    const handleSquareClick = (targetPosition) => {
        if (
            selectedPiece &&
            highlightedSquares.some(
                pos => pos.row === targetPosition.row && pos.col === targetPosition.col
            )
        ) {
            console.log(`Moving piece: ${selectedPiece.name}`);
            console.log(`From position: row=${selectedPiece.position.row}, col=${selectedPiece.position.col}`);
            console.log(`To position: row=${targetPosition.row}, col=${targetPosition.col}`);

            setHighlightedSquares([]);
            setSelectedSquare(null);
            setSelectedPiece(null);
        } else {
            setHighlightedSquares([]);
            setSelectedSquare(null);
            setSelectedPiece(null);
        }
    };

    return (
        <ChessBoard
            board={board}
            onPieceClick={handlePieceClick}
            onSquareClick={handleSquareClick}
            highlightedSquares={highlightedSquares}
            selectedSquare={selectedSquare}
        />
    );
};

export default ChessBoardLogic;
