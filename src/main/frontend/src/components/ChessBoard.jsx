import React, { useState } from 'react';
import Square from './Square.jsx';
import utils from "../utils/utils.js";

import initialChessBoard from "./initialChessBoard.js"
import '../assets/ChessBoard.css'; // 스타일 시트 경로 확인


/**
 * @description ChessBoard Component. Root Component of chessboard.
 *
 * @component
 * @returns {JSX.Element} - 체스판 로직 및 렌더링을 포함한 JSX 요소
 */
function ChessBoard() {
    const [board] = useState(initialChessBoard);
    const [highlightedSquares, setHighlightedSquares] = useState([]);
    const [selectedSquare, setSelectedSquare] = useState(null);
    const selectedPiece = selectedSquare ? board[selectedSquare.row][selectedSquare.col] : null;
    /**
     * @description 기물이 클릭되었을 때 호출되는 함수
     *
     * @param {Object} piece - 클릭된 기물 객체
     */
    const handlePieceClick = (piece) => {
        setHighlightedSquares([]);
        setSelectedSquare(null);

        if (piece) {
            const availableMoves = piece.getAvailableMoves(board);
            utils.dlog(`Selected piece: ${piece.name} at row=${piece.position.row} col=${piece.position.col}`);

            setHighlightedSquares(availableMoves);
            setSelectedSquare(piece.position);
        }
    };

    /**
     * @description 기물이 없는 칸이 클릭되었을 때 호출되는 함수
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
            utils.dlog(`Moving piece: ${selectedPiece.name}`);
            utils.dlog(`From position: row=${selectedPiece.position.row}, col=${selectedPiece.position.col}`);
            utils.dlog(`To position: row=${targetPosition.row}, col=${targetPosition.col}`);

            setHighlightedSquares([]);
            setSelectedSquare(null);
        } else {
            setHighlightedSquares([]);
            setSelectedSquare(null);
        }
    };

    /**
     * 하나의 행(row)을 렌더링하는 함수
     *
     * @param {number} i - 행 인덱스
     * @returns {JSX.Element} - 각 행을 나타내는 JSX 요소
     */
    const renderRow = (i) => {
        return (
            <div key={i} className="board-row">
                {Array.from({ length: 8 }, (_, j) =>
                    <Square
                        piece={board[i][j]}
                        position={{'row': i, 'col': j}}
                        handleClick={board[i][j] ? handlePieceClick : handleSquareClick}
                        isHighlighted={highlightedSquares.some(pos => pos.row === i && pos.col === j)}
                        isSelected={selectedSquare && selectedSquare.row === i && selectedSquare.col === j}
                    />
                )}
            </div>
        );
    };

    return (
        <div className="chess-board">
            {Array.from({ length: 8 }, (_, i) => 7 - i).map((i) => renderRow(i))}
        </div>
    );
};

export default ChessBoard;
