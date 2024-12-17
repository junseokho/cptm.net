import React from 'react';
import utils from "../utils/utils.js";


/**
 * @author 손의현(SONY-STRING)
 * @description Square in chessboard
 * @returns {JSX.Element} - JSX Component of Square
 */
function Square({ piece, position, handleClick, isHighlighted, isSelected }) {
    const isLightBrown = (position.row + position.col) % 2 === 1; // checker pattern
    let squareClasses = isLightBrown ? 'light-brown' : 'white';

    if (isHighlighted) {
        squareClasses += ' highlight';
    }

    if (isSelected) {
        squareClasses += ' selected';
    }
    return(
        <div
            key={`${position.row}-${position.col}`}
            className={`square ${squareClasses}`}
            data-position={`${position.row}-${position.col}`}
            onClick={() => handleClick(piece ? piece : position)}
        >
            {piece && <img src={piece.image} alt={piece.name} className="piece"/>}
        </div>
    );
}


// /**
//  * ChessBoard 컴포넌트 - 체스판과 기물을 렌더링하는 컴포넌트
//  *
//  * @param {Object} props - 컴포넌트의 props
//  * @param {Array} props.board - 8x8 체스판 배열, 각 칸에 기물 객체가 있거나 null
//  * @param {Function} props.onPieceClick - 사용자가 기물을 클릭할 때 호출되는 함수
//  * @param {Function} props.onSquareClick - 사용자가 빈 칸을 클릭할 때 호출되는 함수
//  * @param {Array} props.highlightedSquares - 이동 가능한 칸을 표시하는 배열
//  * @param {Object} props.selectedSquare - 선택된 칸의 위치 객체 {row: number, col: number}
//  * @returns {JSX.Element} - 체스판을 렌더링하는 JSX 요소
//  */
// const ChessBoard = ({board, onPieceClick, onSquareClick, highlightedSquares, selectedSquare }) => {
//
//     /**
//      * 하나의 칸(square)을 렌더링하는 함수
//      *
//      * @param {number} r - 열 인덱스 (column)
//      * @param {number} c - 행 인덱스 (row)
//      * @returns {JSX.Element} - 각 체스판 칸을 나타내는 JSX 요소
//      */
//     const renderSquare = (r, c) => {
//         const piece = board[r][c];
//         const isLightBrown = (r + c) % 2 === 1; // 칸 색상 설정 (체스판 패턴)
//         let squareClasses = isLightBrown ? 'light-brown' : 'white';
//
//         // 강조된 칸인지 확인
//         if (highlightedSquares.some(pos => pos.row === r && pos.col === c)) {
//             squareClasses += ' highlight';
//         }
//
//         // 선택된 칸인지 확인
//         if (selectedSquare && selectedSquare.row === r && selectedSquare.col === c) {
//             squareClasses += ' selected';
//         }
//
//         return (
//             <div
//                 key={`${r}-${c}`}
//                 className={`square ${squareClasses}`}
//                 data-position={`${r}-${c}`}
//                 onClick={() => {
//                     if (piece) {
//                         onPieceClick(piece, { row: r, col: c });
//                     } else {
//                         onSquareClick({ row: r, col: c });
//                     }
//                 }}
//             >
//                 {piece && <img src={piece.image} alt={piece.name} className="piece" />}
//             </div>
//         );
//     };
//
//     /**
//      * 하나의 행(row)을 렌더링하는 함수
//      *
//      * @param {number} r - 행 인덱스
//      * @returns {JSX.Element} - 각 행을 나타내는 JSX 요소
//      */
//     const renderRow = (r) => {
//         return (
//             <div key={r} className="board-row">
//                 {Array.from({ length: 8 }, (_, c) => renderSquare(r, c))}
//             </div>
//         );
//     };
//
//     return (
//         <div className="chess-board">
//             {Array.from({length: 8}, (_, r) => 7 - r).map((r) =>
//                 <div key={r} className="board-row">
//                     {Array.from({length: 8}, (_, c) => renderSquare(r, c))}
//                 </div>
//             )}
//         </div>
//     );
// };

export default Square;
