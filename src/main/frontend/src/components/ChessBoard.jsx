import React from 'react';
import '../assets/ChessBoard.css'; // 스타일 시트 경로 확인

/**
 * ChessBoard 컴포넌트 - 체스판과 기물을 렌더링하는 컴포넌트
 *
 * @param {Object} props - 컴포넌트의 props
 * @param {Array} props.board - 8x8 체스판 배열, 각 칸에 기물 객체가 있거나 null
 * @param {Function} props.onPieceClick - 사용자가 기물을 클릭할 때 호출되는 함수
 * @param {Function} props.onSquareClick - 사용자가 빈 칸을 클릭할 때 호출되는 함수
 * @param {Array} props.highlightedSquares - 이동 가능한 칸을 표시하는 배열
 * @param {Object} props.selectedSquare - 선택된 칸의 위치 객체 {col: number, row: number}
 * @returns {JSX.Element} - 체스판을 렌더링하는 JSX 요소
 */
const ChessBoard = ({ board, onPieceClick, onSquareClick, highlightedSquares, selectedSquare }) => {

    /**
     * 하나의 칸(square)을 렌더링하는 함수
     *
     * @param {number} i - 열 인덱스 (row)
     * @param {number} j - 행 인덱스 (col)
     * @returns {JSX.Element} - 각 체스판 칸을 나타내는 JSX 요소
     */
    const renderSquare = (i, j) => {
        const piece = board[i][j];
        const isLightBrown = (i + j) % 2 === 1; // 칸 색상 설정 (체스판 패턴)
        let squareClasses = isLightBrown ? 'light-brown' : 'white';

        // 강조된 칸인지 확인
        if (highlightedSquares.some(pos => pos.col === j && pos.row === i)) {
            squareClasses += ' highlight';
        }

        // 선택된 칸인지 확인
        if (selectedSquare && selectedSquare.col === j && selectedSquare.row === i) {
            squareClasses += ' selected';
        }

        return (
            <div
                key={`${i}-${j}`}
                className={`square ${squareClasses}`}
                data-position={`${i}-${j}`}
                onClick={() => {
                    if (piece) {
                        onPieceClick(piece, { row: j, col: i });
                    } else {
                        onSquareClick({ row: j, col: i });
                    }
                }}
            >
                {piece && <img src={piece.image} alt={piece.name} className="piece" />}
            </div>
        );
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
                {Array.from({ length: 8 }, (_, j) => renderSquare(i, j))}
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
