import { Rook, Knight, Bishop, Queen, King, Pawn } from './Piece.jsx';


/**
 * @description 초기 체스판 설정을 정의한 배열
 * @type {Array<Array<Piece|null>>}
 */
const initialBoard = [
    // white pieces
    [
        new Rook    ('Rook',    { row: 0, col: 0 }, 'white', '/src/assets/wR.svg'),
        new Knight  ('Knight',  { row: 0, col: 1 }, 'white', '/src/assets/wN.svg'),
        new Bishop  ('Bishop',  { row: 0, col: 2 }, 'white', '/src/assets/wB.svg'),
        new Queen   ('Queen',   { row: 0, col: 3 }, 'white', '/src/assets/wQ.svg'),
        new King    ('King',    { row: 0, col: 4 }, 'white', '/src/assets/wK.svg'),
        new Bishop  ('Bishop',  { row: 0, col: 5 }, 'white', '/src/assets/wB.svg'),
        new Knight  ('Knight',  { row: 0, col: 6 }, 'white', '/src/assets/wN.svg'),
        new Rook    ('Rook',    { row: 0, col: 7 }, 'white', '/src/assets/wR.svg'),
    ],
    // white pawns
    Array(8).fill(null).map((_, idx) => new Pawn('Pawn', { row: 1, col: idx }, 'white', '/src/assets/wP.svg')),
    // empty squares
    Array(8).fill(null),
    Array(8).fill(null),
    Array(8).fill(null),
    Array(8).fill(null),
    // black pawns
    Array(8).fill(null).map((_, idx) => new Pawn('Pawn', { row: 6, col: idx }, 'black', '/src/assets/bP.svg')),
    // black pieces
    [
        new Rook    ('Rook',      { row: 7, col: 0 }, 'black', '/src/assets/bR.svg'),
        new Knight  ('Knight',    { row: 7, col: 1 }, 'black', '/src/assets/bN.svg'),
        new Bishop  ('Bishop',    { row: 7, col: 2 }, 'black', '/src/assets/bB.svg'),
        new Queen   ('Queen',     { row: 7, col: 3 }, 'black', '/src/assets/bQ.svg'),
        new King    ('King',      { row: 7, col: 4 }, 'black', '/src/assets/bK.svg'),
        new Bishop  ('Bishop',    { row: 7, col: 5 }, 'black', '/src/assets/bB.svg'),
        new Knight  ('Knight',    { row: 7, col: 6 }, 'black', '/src/assets/bN.svg'),
        new Rook    ('Rook',      { row: 7, col: 7 }, 'black', '/src/assets/bR.svg'),
    ]
];

export default initialBoard;