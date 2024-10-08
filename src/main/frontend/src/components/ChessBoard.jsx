import React from 'react';
import '../assets/ChessBoard.css'; // 스타일 시트 경로 확인

const ChessBoard = ({ board, onPieceClick, highlightedSquares, selectedSquare }) => {
    const renderSquare = (i, j) => {
        const piece = board[i][j];
        const isLightBrown = (i + j) % 2 === 1;
        let squareClasses = isLightBrown ? 'light-brown' : 'white';

        // 강조된 칸인지 확인
        if (highlightedSquares.some(pos => pos.x === j && pos.y === i)) {
            squareClasses += ' highlight';
        }

        // 선택된 칸인지 확인
        if (selectedSquare && selectedSquare.x === j && selectedSquare.y === i) {
            squareClasses += ' selected';
        }

        return (
            <div
                key={`${i}-${j}`}
                className={`square ${squareClasses}`}
                data-position={`${i}-${j}`}
                onClick={() => {
                    console.log('Square clicked:', i, j, piece); // 클릭된 칸과 기물 확인
                    onPieceClick(piece, { x: j, y: i }); // 위치 정보도 전달
                }}
            >
                {piece && <img src={piece.image} alt={piece.name} className="piece" />}
            </div>
        );
    };

    const renderRow = (i) => {
        return (
            <div key={i} className="board-row">
                {Array.from({ length: 8 }, (_, j) => renderSquare(i, j))}
            </div>
        );
    };

    return (
        <div className="chess-board">
            {Array.from({ length: 8 }, (_, i) => renderRow(i))}
        </div>
    );
};

export default ChessBoard;
