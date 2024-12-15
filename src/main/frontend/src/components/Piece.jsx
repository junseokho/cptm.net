

/**
 * Represents a chess piece.
 * @class
 */
export class Piece {
    /**
     * Creates a chess piece.
     * @param {string} name The name of the piece.
     * @param {Object} position The current position of the piece.
     * @param {number} position.row The row position.
     * @param {number} position.col The column position.
     * @param {string} color The color of the piece ('white' or 'black').
     * @param {string} image The image URL for the piece.
     * @param {boolean} hasMoved true if piece hasn't moved or is not initial position
     */
    constructor(name, position, color, image, hasMoved = false) {
        this.name = name;
        this.position = position;
        this.color = color;
        this.image = image;
        this.hasMoved = hasMoved;
    }

    /**
     * Checks if a move to the given position is valid.
     * @param {Object} position - The target position.
     * @param {number} position.row - The row to move to.
     * @param {number} position.col - The column to move to.
     * @param {Array} board - The current board state.
     * @returns {boolean} - Returns true if the move is valid.
     */
    isValidMove(position, board) {
        if (position.row < 0 || position.row > 7 || position.col < 0 || position.col > 7) return false;
        const pieceAtDestination = board[position.row][position.col];
        return !(pieceAtDestination && pieceAtDestination.color === this.color);


    }

    /**
     * Gets valid moves along specified directions.
     * @param {Array} directions - Array of direction objects.
     * @param {Array} board - The current state of the board.
     * @returns {Array} - A list of valid move positions.
     */
    get_valid_moves_on_way(directions, board) {
        let moves = [];

        for (const direction of directions) {
            let row = this.position.row;
            let col = this.position.col;

            while (true) {
                row += direction.row;
                col += direction.col;
                const newPosition = { row, col };

                if (!this.isValidMove(newPosition, board)) break;

                moves.push(newPosition);

                const pieceAtNewPosition = board[row][col];
                if (pieceAtNewPosition) {
                    if (pieceAtNewPosition.color !== this.color) moves.push(newPosition);
                    break;
                }
            }
        }
        return moves;
    }
}

/**
 * Represents a Rook chess piece.
 * @extends Piece
 */
export class Rook extends Piece {
    /**
     * Gets the available moves for the Rook.
     * @param {Array} board - The current state of the board.
     * @returns {Array} - A list of valid move positions.
     */
    getAvailableMoves(board) {
        const directions = [
            { row: 1, col: 0 },
            { row: -1, col: 0 },
            { row: 0, col: 1 },
            { row: 0, col: -1 }
        ];
        return this.get_valid_moves_on_way(directions, board);
    }
}

/**
 * Represents a Bishop chess piece.
 * @extends Piece
 */
export class Bishop extends Piece {
    /**
     * Gets the available moves for the Bishop.
     * @param {Array} board - The current state of the board.
     * @returns {Array} - A list of valid move positions.
     */
    getAvailableMoves(board) {
        const directions = [
            { row: 1, col: 1 },  // down-right
            { row: 1, col: -1 }, // down-left
            { row: -1, col: 1 }, // up-right
            { row: -1, col: -1 } // up-left
        ];
        return this.get_valid_moves_on_way(directions, board);
    }
}

/**
 * Represents a Queen chess piece.
 * @extends Piece
 */
export class Queen extends Piece {
    /**
     * Gets the available moves for the Queen.
     * @param {Array} board - The current state of the board.
     * @returns {Array} - A list of valid move positions.
     */
    getAvailableMoves(board) {
        const directions = [
            { row: 1, col: 0 },  // right
            { row: -1, col: 0 }, // left
            { row: 0, col: 1 },  // down
            { row: 0, col: -1 }, // up
            { row: 1, col: 1 },  // down-right
            { row: 1, col: -1 }, // down-left
            { row: -1, col: 1 }, // up-right
            { row: -1, col: -1 } // up-left
        ];
        return this.get_valid_moves_on_way(directions, board);
    }
}

/**
 * Represents a Knight chess piece.
 * @extends Piece
 */
export class Knight extends Piece {
    /**
     * Gets the available moves for the Knight.
     * @param {Array} board - The current state of the board.
     * @returns {Array} - A list of valid move positions.
     */
    getAvailableMoves(board) {
        let moves = [];
        const potentialMoves = [
            { row: this.position.row + 2, col: this.position.col + 1 },
            { row: this.position.row + 2, col: this.position.col - 1 },
            { row: this.position.row - 2, col: this.position.col + 1 },
            { row: this.position.row - 2, col: this.position.col - 1 },
            { row: this.position.row + 1, col: this.position.col + 2 },
            { row: this.position.row + 1, col: this.position.col - 2 },
            { row: this.position.row - 1, col: this.position.col + 2 },
            { row: this.position.row - 1, col: this.position.col - 2 }
        ];

        for (const move of potentialMoves) {
            if (this.isValidMove(move, board)) {
                moves.push(move);
            }
        }

        return moves;
    }
}

/**
 * Represents a Pawn chess piece.
 * @extends Piece
 */
export class Pawn extends Piece {
    /**
     * Gets the available moves for the Pawn, including en passant and promotion.
     * @param {Array} board - The current state of the board.
     * @param {Object} Chessboard - 체스보드 상태 객체
     * @returns {Array} - A list of valid move positions.
     */
    getAvailableMoves(board, Chessboard) {
        let moves = [];
        const { moveHistory } = Chessboard;
        const direction = this.color === 'white' ? 1 : -1;

        // 1칸 전진
        const oneStepForward = { row: this.position.row + direction, col: this.position.col };
        if (this.isValidMove(oneStepForward, board) && !board[oneStepForward.row]?.[oneStepForward.col]) {
            moves.push({
                ...oneStepForward,
                type: (oneStepForward.row === 0 || oneStepForward.row === 7) ? 'promotion' : undefined,
            });
        }

        // 2칸 전진 (초기 위치에서만 가능)
        const startingRow = this.color === 'white' ? 1 : 6;
        const twoStepsForward = { row: this.position.row + 2 * direction, col: this.position.col };
        if (this.position.row === startingRow &&
            !board[oneStepForward.row]?.[oneStepForward.col] &&
            !board[twoStepsForward.row]?.[twoStepsForward.col]
        ) {
            moves.push(twoStepsForward);
        }

        // 대각선 공격 및 앙파상
        const leftDiagonal = { row: this.position.row + direction, col: this.position.col - 1 };
        const rightDiagonal = { row: this.position.row + direction, col: this.position.col + 1 };

        // 앙파상 처리
        if (moveHistory.length > 0) {
            const lastMove = moveHistory[moveHistory.length - 1]; // 마지막 이동 기록 가져오기
            const [start, end] = lastMove.split('-'); // 기보 형식: "e2-e4"
            const startRow = parseInt(start[1])-1;
            const startCol = start.charCodeAt(0) - 'a'.charCodeAt(0);
            const endRow = parseInt(end[1])-1;
            const endCol = end.charCodeAt(0) - 'a'.charCodeAt(0);

            const movedPawn = board[endRow]?.[endCol];
            if (
                movedPawn instanceof Pawn &&
                movedPawn.color !== this.color &&
                Math.abs(startRow - endRow) === 2 &&
                startCol === endCol &&
                ((this.color === 'white' && this.position.row === 4) ||
                    (this.color === 'black' && this.position.row === 3)) // 앙파상 위치 조건 추가
            ) {
                // 왼쪽 대각선 앙파상
                if (this.position.row === endRow && this.position.col - 1 === endCol) {
                    moves.push({
                        row: endRow + direction,
                        col: endCol,
                        type: 'enPassant',
                        enPassantCapture: { row: endRow, col: endCol },
                    });
                }
                // 오른쪽 대각선 앙파상
                if (this.position.row === endRow && this.position.col + 1 === endCol) {
                    moves.push({
                        row: endRow + direction,
                        col: endCol,
                        type: 'enPassant',
                        enPassantCapture: { row: endRow, col: endCol },
                    });
                }
            }
        }

        // 일반 대각선 공격
        const attackSquares = [leftDiagonal, rightDiagonal];
        for (const square of attackSquares) {
            if (this.isValidMove(square, board)) {
                const pieceAtSquare = board[square.row]?.[square.col];
                if (pieceAtSquare && pieceAtSquare.color !== this.color) {
                    moves.push({
                        ...square,
                        type: (square.row === 0 || square.row === 7) ? 'promotion' : undefined,
                    });
                }
            }
        }

        return moves;
    }

}




/**
 * Represents a King chess piece.
 * @extends Piece
 */
export class King extends Piece {
    /**
     * Gets the available moves for the King, including castling options.
     * @param {Array} board - The current state of the board.
     * @param {Object} Chessboard - 체스보드 상태 객체
     * @returns {Array} - A list of valid move positions.
     */
    getAvailableMoves(board, Chessboard) {

        // 기존 킹의 이동 경로 로직
        let moves = [];
        const potentialMoves = [
            { row: this.position.row + 1, col: this.position.col }, // 아래
            { row: this.position.row - 1, col: this.position.col }, // 위
            { row: this.position.row, col: this.position.col + 1 }, // 오른쪽
            { row: this.position.row, col: this.position.col - 1 }, // 왼쪽
            { row: this.position.row + 1, col: this.position.col + 1 }, // 대각선 아래-오른쪽
            { row: this.position.row + 1, col: this.position.col - 1 }, // 대각선 아래-왼쪽
            { row: this.position.row - 1, col: this.position.col + 1 }, // 대각선 위-오른쪽
            { row: this.position.row - 1, col: this.position.col - 1 }  // 대각선 위-왼쪽
        ];

        for (const move of potentialMoves) {
            if (this.isValidMove(move, board)) {
                // 킹이 이동한 위치가 공격받지 않는 경우만 이동 가능
                if (!this.isUnderAttack(move, Chessboard)) {
                    moves.push(move);
                }
            }
        }
        // 캐슬링 처리
        if (!this.hasMoved && !this.isUnderAttack(this.position, Chessboard)) {
            // 킹사이드 캐슬링
            const rookKingSide = board[this.position.row]?.[7];
            if (rookKingSide instanceof Rook && !rookKingSide.hasMoved) {
                if (
                    !board[this.position.row][5] &&
                    !board[this.position.row][6] &&
                    !this.isUnderAttack({ row: this.position.row, col: 5 }, Chessboard) &&
                    !this.isUnderAttack({ row: this.position.row, col: 6 }, Chessboard)
                ) {
                    moves.push({
                        row: this.position.row,
                        col: 6,
                        type: 'castling', // 캐슬링 신호 추가
                        rookStart: { row: this.position.row, col: 7 },
                        rookEnd: { row: this.position.row, col: 5 },
                    });
                }
            }

            // 퀸사이드 캐슬링
            const rookQueenSide = board[this.position.row]?.[0];
            if (rookQueenSide instanceof Rook && !rookQueenSide.hasMoved) {
                if (
                    !board[this.position.row][1] &&
                    !board[this.position.row][2] &&
                    !board[this.position.row][3] &&
                    !this.isUnderAttack({ row: this.position.row, col: 2 }, Chessboard) &&
                    !this.isUnderAttack({ row: this.position.row, col: 3 }, Chessboard)
                ) {
                    moves.push({
                        row: this.position.row,
                        col: 2,
                        type: 'castling', // 캐슬링 신호 추가
                        rookStart: { row: this.position.row, col: 0 },
                        rookEnd: { row: this.position.row, col: 3 },
                    });
                }
            }
        }

        return moves

    }

    /**
     * Checks if the given position is under attack by enemy pieces.
     * @param {Object} position - The position to check.
     * @param {Object} Chessboard - 체스보드 상태 객체
     * @returns {boolean} - True if the position is under attack.
     */
    isUnderAttack(position, Chessboard) {
        const { board } = Chessboard;

        // 1. 직선 및 대각선 방향에서 공격 확인
        const directions = [
            { row: 1, col: 0 }, { row: -1, col: 0 }, // 상하
            { row: 0, col: 1 }, { row: 0, col: -1 }, // 좌우
            { row: 1, col: 1 }, { row: 1, col: -1 }, // 대각선 아래
            { row: -1, col: 1 }, { row: -1, col: -1 } // 대각선 위
        ];

        for (const direction of directions) {
            let row = position.row;
            let col = position.col;

            while (true) {
                row += direction.row;
                col += direction.col;
                if (row < 0 || row > 7 || col < 0 || col > 7) break; // 경계 확인

                const piece = board[row]?.[col];
                if (!piece) continue; // 빈 칸이면 다음 칸 확인
                if (piece.color === this.color) break; // 같은 색 기물이면 종료

                // 공격 가능 기물 체크
                if (
                    (piece instanceof Queen) ||
                    (piece instanceof Rook && (direction.row === 0 || direction.col === 0)) ||
                    (piece instanceof Bishop && direction.row !== 0 && direction.col !== 0)
                ) {
                    return true;
                }
                break;
            }
        }

        // 2. 나이트 공격 확인
        const knightMoves = [
            { row: 2, col: 1 }, { row: 2, col: -1 },
            { row: -2, col: 1 }, { row: -2, col: -1 },
            { row: 1, col: 2 }, { row: 1, col: -2 },
            { row: -1, col: 2 }, { row: -1, col: -2 }
        ];

        for (const move of knightMoves) {
            const newRow = position.row + move.row;
            const newCol = position.col + move.col;
            if (newRow < 0 || newRow > 7 || newCol < 0 || newCol > 7) continue;
            const piece = board[newRow]?.[newCol];
            if (piece instanceof Knight && piece.color !== this.color) {
                return true;
            }
        }

        // 3. 폰 공격 확인
        const pawnAttackDirections = this.color === 'white'
            ? [{ row: 1, col: 1 }, { row: 1, col: -1 }]
            : [{ row: -1, col: 1 }, { row: -1, col: -1 }];

        for (const direction of pawnAttackDirections) {
            const newRow = position.row + direction.row;
            const newCol = position.col + direction.col;
            if (newRow < 0 || newRow > 7 || newCol < 0 || newCol > 7) continue;
            const piece = board[newRow]?.[newCol];
            if (piece instanceof Pawn && piece.color !== this.color) {
                return true;
            }
        }

        return false;
    }


}

/**
 * 킹이 공격받지 않도록 이동 가능한 칸을 필터링
 * @param {Array} moves - 원래 이동 가능 칸 목록
 * @param {Object} board - 현재 체스판 배열
 * @param {Object} piece - 선택된 기물
 * @returns {Array} - 킹이 안전한 이동 가능 칸 목록
 */
/**
 * 킹이 공격받지 않도록 이동 가능한 칸을 필터링
 * @param {Array} moves - 원래 이동 가능 칸 목록
 * @param {Object} Chessboard - 체스보드 상태 객체
 * @param {Object} piece - 선택된 기물
 * @returns {Array} - 킹이 안전한 이동 가능 칸 목록
 */
function filterValidMoves(moves, Chessboard, piece) {
    const safeMoves = [];


    for (const move of moves) {
        // 체스판 복제
        const newBoard = Chessboard.board.map(row => [...row]);

        // 기물을 임시로 이동
        newBoard[piece.position.row][piece.position.col] = null;
        newBoard[move.row][move.col] = piece;

        // 기물 위치 임시 업데이트
        const originalPosition = { ...piece.position };
        piece.position = { row: move.row, col: move.col };

        // 킹의 위치 계산
        const kingPosition = piece instanceof King
            ? { row: move.row, col: move.col } // 킹이 직접 이동하는 경우
            : findKingPosition(newBoard, piece.color);

        // 킹이 공격받는지 확인
        const isUnderAttack = piece.isUnderAttack(kingPosition, { board: newBoard });

        // 킹이 공격받지 않으면 안전한 이동 경로로 추가
        if (!isUnderAttack) {
            safeMoves.push(move);
        }

        // 기물 위치 복원
        piece.position = originalPosition;
    }

    return safeMoves;
}

/**
 * 체스판에서 특정 색상의 킹 위치를 찾는 함수
 * @param {Array<Array<Piece|null>>} board - 현재 체스판 배열
 * @param {string} color - 찾으려는 킹의 색상 ('white' 또는 'black')
 * @returns {Object|null} - 킹의 위치 객체 { row, col } 또는 null (킹이 없는 경우)
 */
export function findKingPosition(board, color) {
    for (let row = 0; row < board.length; row++) {
        for (let col = 0; col < board[row].length; col++) {
            const piece = board[row][col];
            if (piece instanceof King && piece.color === color) {
                return { row, col };
            }
        }
    }
    return null; // 킹이 없는 경우
}

