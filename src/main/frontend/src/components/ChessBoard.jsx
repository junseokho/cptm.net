import React from 'react';
import '../assets/ChessBoard.css';

const ChessBoard = ({ board, onPieceClick }) => {
    return (
        <div className="chess-board">
            {board.map((row, rowIndex) => (
                <div className="row" key={rowIndex}>
                    {row.map((piece, colIndex) => (
                        <div className="cell" key={colIndex} onClick={() => onPieceClick(piece, rowIndex, colIndex)}>
                            {piece ? <img src={piece.image} alt={piece.name} className="piece-image" /> : ''}
                        </div>
                    ))}
                </div>
            ))}
        </div>
    );
};

export default ChessBoard;
