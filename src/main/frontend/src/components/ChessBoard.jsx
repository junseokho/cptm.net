import React, {useState} from 'react';
import '../assets/ChessBoard.css';
import initialBoard from '../components/initialChessBoard.js';
import {Bishop, Knight, Queen, Rook} from "./Piece.jsx";


/**
 * ChessBoard 컴포넌트
 * 체스판 상태를 관리하고 렌더링하는 컴포넌트
 *
 * @component
 * @state {Object} chessboard - 체스판 상태를 나타내는 객체
 * @property {Array} chessboard.board - 현재 체스판 배열
 * @property {Array} chessboard.moveHistory - 이동 기록 배열
 * @property {string} chessboard.turn - 현재 턴 ('white' 또는 'black')
 * @state {Object|null} selectedPiece - 선택된 기물 객체와 위치
 * @state {Array} validMoves - 선택된 기물의 유효한 이동 경로
 *
 * @returns {JSX.Element} ChessBoard 컴포넌트
 */
const ChessBoard = () => {
    const [chessboard, setChessboard] = useState({
        board: initialBoard, // 체스판 초기 상태
        moveHistory: [], // 이동 기록
        turn: 'white', // 현재 턴
    });
    const [selectedPiece, setSelectedPiece] = useState(null); // 선택된 기물
    const [validMoves, setValidMoves] = useState([]); // 유효한 이동 경로

    /**
     * 좌표를 체스 기보 형식으로 변환
     *
     * @param {Object} position - 변환할 좌표
     * @param {number} position.row - 체스판의 행
     * @param {number} position.col - 체스판의 열
     * @returns {string} 변환된 체스 기보 형식 (예: "e2")
     */
    const convertToChessNotation = ({ row, col }) => {
        const files = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h']; // x축 알파벳
        return `${files[col]}${row+1}`; // y축 숫자는 위에서부터 시작
    };

    /**
     * 기물을 클릭했을 때 호출되는 함수
     *
     * @param {Object} piece - 클릭된 기물 객체
     * @param {Object} position - 클릭된 기물의 현재 위치
     * @param {number} position.row - 기물의 행
     * @param {number} position.col - 기물의 열
     */
    const handlePieceClick = (piece, position) => {
        if (piece.color !== chessboard.turn) return;

        setSelectedPiece({ piece, position });

        // 선택된 기물의 유효한 이동 경로 설정
        setValidMoves(piece.getAvailableMoves(chessboard.board, chessboard));
    };

    /**
     * 빈 칸 또는 상대 기물을 클릭했을 때 호출되는 함수
     *
     * @param {Object} targetPosition - 클릭된 칸의 위치
     * @param {number} targetPosition.row - 클릭된 칸의 행
     * @param {number} targetPosition.col - 클릭된 칸의 열
     */
    const handleSquareClick = (targetPosition) => {
        if (!selectedPiece) return;

        const { piece, position } = selectedPiece;

        // 유효한 이동인지 확인
        const move = validMoves.find(
            move => move.row === targetPosition.row && move.col === targetPosition.col
        );

        if (move) {
            const newBoard = chessboard.board.map(row => [...row]);

            // 캐슬링 처리
            if (move.type === 'castling') {
                const { rookStart, rookEnd } = move;

                // 룩 이동 처리
                const rook = newBoard[rookStart.row][rookStart.col];
                newBoard[rookStart.row][rookStart.col] = null; // 기존 룩 위치 비우기
                newBoard[rookEnd.row][rookEnd.col] = rook; // 새로운 룩 위치 설정
                rook.position = rookEnd; // 룩 객체 위치 업데이트
            }

            // 앙파상 처리
            if (move.type === 'enPassant') {
                const capturedPawnPosition = {
                    row: position.row, // 이동한 폰의 초기 행 위치와 동일
                    col: targetPosition.col // 목표 열에 위치한 상대 폰 제거
                };
                newBoard[capturedPawnPosition.row][capturedPawnPosition.col] = null; // 앙파상으로 제거된 폰
            }

            // 프로모션 처리
            if (move.type === 'promotion') {
                const selectedPiece = prompt('Enter piece for promotion (queen, rook, bishop, knight):', 'queen');
                newBoard[targetPosition.row][targetPosition.col] = createPromotedPiece(selectedPiece, targetPosition, piece.color);
            } else {
                // 일반 이동 처리
                newBoard[targetPosition.row][targetPosition.col] = piece;
                piece.position = targetPosition;
            }

            // 기존 위치 비우기
            newBoard[position.row][position.col] = null;

            // 상태 업데이트
            setChessboard((prevChessboard) => ({
                ...prevChessboard,
                board: newBoard,
                moveHistory: [
                    ...prevChessboard.moveHistory,
                    `${convertToChessNotation(position)}-${convertToChessNotation(targetPosition)}`
                ],
                turn: prevChessboard.turn === 'white' ? 'black' : 'white',
            }));

            setSelectedPiece(null);
            setValidMoves([]);
        } else {
            alert("Invalid move!");
        }
    };



    const createPromotedPiece = (pieceType, position, color) => {
        switch (pieceType.toLowerCase()) {
            case 'queen':
                return new Queen('Queen', position, color, color === 'white' ? '/src/assets/wQ.svg' : '/src/assets/bQ.svg');
            case 'rook':
                return new Rook('Rook', position, color, color === 'white' ? '/src/assets/wR.svg' : '/src/assets/bR.svg');
            case 'bishop':
                return new Bishop('Bishop', position, color, color === 'white' ? '/src/assets/wB.svg' : '/src/assets/bB.svg');
            case 'knight':
                return new Knight('Knight', position, color, color === 'white' ? '/src/assets/wN.svg' : '/src/assets/bN.svg');
            default:
                alert('Invalid piece selected for promotion!');
                return new Queen('Queen', position, color, color === 'white' ? '/src/assets/wQ.svg' : '/src/assets/bQ.svg');
        }
    };


    /**
     * 체스판의 한 칸을 렌더링
     *
     * @param {number} i - 행 인덱스
     * @param {number} j - 열 인덱스
     * @returns {JSX.Element} 렌더링된 칸
     */
    const renderSquare = (i, j) => {
        const piece = chessboard.board[i][j];
        const isLightBrown = (i + j) % 2 === 1;
        let squareClasses = isLightBrown ? 'light-brown' : 'white';

        // 유효 이동 경로 강조
        if (validMoves.some(pos => pos.row === i && pos.col === j)) {
            squareClasses += ' highlight';
        }

        // 선택된 기물 강조
        if (selectedPiece && selectedPiece.position.row === i && selectedPiece.position.col === j) {
            squareClasses += ' selected';
        }

        return (
            <div
                key={`${i}-${j}`}
                className={`square ${squareClasses}`}
                onClick={() => {
                    if (piece && piece.color === chessboard.turn) {
                        handlePieceClick(piece, { row: i, col: j });
                    } else {
                        handleSquareClick({ row: i, col: j });
                    }
                }}
            >
                {piece && <img src={piece.image} alt={piece.name} className="piece" />}
            </div>
        );
    };

    /**
     * 체스판의 한 행을 렌더링
     *
     * @param {number} i - 행 인덱스
     * @returns {JSX.Element} 렌더링된 행
     */
    const renderRow = (i) => (
        <div key={i} className="board-row">
            {Array.from({ length: 8 }, (_, j) => renderSquare(i, j))}
        </div>
    );

    return (
        <div className="chess-board-container">
            <div className="chess-board">
                {Array.from({ length: 8 }, (_, i) => renderRow(7 - i))}
            </div>
            <div className="move-history">
                <h3>Move History</h3>
                <ol>
                    {chessboard.moveHistory.map((move, index) => (
                        <li key={index}>{move}</li>
                    ))}
                </ol>
            </div>
        </div>
    );
};

export default ChessBoard;
