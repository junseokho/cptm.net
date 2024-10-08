import React from 'react';

export class Piece {
    constructor(name, position, color, image) {
        this.name = name;
        this.position = position;
        this.color = color;
        this.image = image;
    }

    isValidMove(position, board) {
        if (position.x < 0 || position.x > 7 || position.y < 0 || position.y > 7) return false;
        const pieceAtDestination = board[position.y][position.x];
        return !(pieceAtDestination && (pieceAtDestination.color === this.color));

    }
}

//룩의 이동 범위 로직
export class Rook extends Piece {
    getAvailableMoves(board) {
        let moves = [];

        // 오른쪽으로 이동
        for (let x = this.position.x + 1; x < 8; x++) {
            const newPosition = { x, y: this.position.y };
            if (!this.isValidMove(newPosition, board)) break;
            moves.push(newPosition);

            const pieceAtNewPosition = board[newPosition.y][newPosition.x];
            if (pieceAtNewPosition) {
                if (pieceAtNewPosition.color !== this.color) moves.push(newPosition); // 적 기물이면 그 칸에 멈춤
                break; // 기물이 있으면 이후 칸으로는 이동 불가
            }
        }

        // 왼쪽으로 이동
        for (let x = this.position.x - 1; x >= 0; x--) {
            const newPosition = { x, y: this.position.y };
            if (!this.isValidMove(newPosition, board)) break;
            moves.push(newPosition);

            const pieceAtNewPosition = board[newPosition.y][newPosition.x];
            if (pieceAtNewPosition) {
                if (pieceAtNewPosition.color !== this.color) moves.push(newPosition);
                break;
            }
        }

        // 위로 이동
        for (let y = this.position.y - 1; y >= 0; y--) {
            const newPosition = { x: this.position.x, y };
            if (!this.isValidMove(newPosition, board)) break;
            moves.push(newPosition);

            const pieceAtNewPosition = board[newPosition.y][newPosition.x];
            if (pieceAtNewPosition) {
                if (pieceAtNewPosition.color !== this.color) moves.push(newPosition);
                break;
            }
        }

        // 아래로 이동
        for (let y = this.position.y + 1; y < 8; y++) {
            const newPosition = { x: this.position.x, y };
            if (!this.isValidMove(newPosition, board)) break;
            moves.push(newPosition);

            const pieceAtNewPosition = board[newPosition.y][newPosition.x];
            if (pieceAtNewPosition) {
                if (pieceAtNewPosition.color !== this.color) moves.push(newPosition);
                break;
            }
        }

        return moves;
    }
}

//비숍의 이동 범위 로직
export class Bishop extends Piece {
    getAvailableMoves(board) {
        let moves = [];

        // 오른쪽 위 대각선 이동
        for (let x = this.position.x + 1, y = this.position.y - 1; x < 8 && y >= 0; x++, y--) {
            const newPosition = { x, y };
            if (!this.isValidMove(newPosition, board)) break;
            moves.push(newPosition);

            const pieceAtNewPosition = board[newPosition.y][newPosition.x];
            if (pieceAtNewPosition) {
                if (pieceAtNewPosition.color !== this.color) moves.push(newPosition);
                break;
            }
        }

        // 오른쪽 아래 대각선 이동
        for (let x = this.position.x + 1, y = this.position.y + 1; x < 8 && y < 8; x++, y++) {
            const newPosition = { x, y };
            if (!this.isValidMove(newPosition, board)) break;
            moves.push(newPosition);

            const pieceAtNewPosition = board[newPosition.y][newPosition.x];
            if (pieceAtNewPosition) {
                if (pieceAtNewPosition.color !== this.color) moves.push(newPosition);
                break;
            }
        }

        // 왼쪽 위 대각선 이동
        for (let x = this.position.x - 1, y = this.position.y - 1; x >= 0 && y >= 0; x--, y--) {
            const newPosition = { x, y };
            if (!this.isValidMove(newPosition, board)) break;
            moves.push(newPosition);

            const pieceAtNewPosition = board[newPosition.y][newPosition.x];
            if (pieceAtNewPosition) {
                if (pieceAtNewPosition.color !== this.color) moves.push(newPosition);
                break;
            }
        }

        // 왼쪽 아래 대각선 이동
        for (let x = this.position.x - 1, y = this.position.y + 1; x >= 0 && y < 8; x--, y++) {
            const newPosition = { x, y };
            if (!this.isValidMove(newPosition, board)) break;
            moves.push(newPosition);

            const pieceAtNewPosition = board[newPosition.y][newPosition.x];
            if (pieceAtNewPosition) {
                if (pieceAtNewPosition.color !== this.color) moves.push(newPosition);
                break;
            }
        }

        return moves;
    }
}

//나이트의 이동 범위 로직
export class Knight extends Piece {
    getAvailableMoves(board) {
        let moves = [];
        const potentialMoves = [
            { x: this.position.x + 2, y: this.position.y + 1 },
            { x: this.position.x + 2, y: this.position.y - 1 },
            { x: this.position.x - 2, y: this.position.y + 1 },
            { x: this.position.x - 2, y: this.position.y - 1 },
            { x: this.position.x + 1, y: this.position.y + 2 },
            { x: this.position.x + 1, y: this.position.y - 2 },
            { x: this.position.x - 1, y: this.position.y + 2 },
            { x: this.position.x - 1, y: this.position.y - 2 }
        ];

        // 각 가능한 이동 위치가 유효한지 확인
        for (const move of potentialMoves) {
            if (this.isValidMove(move, board)) {
                moves.push(move);
            }
        }

        return moves;
    }
}

//폰의 이동 범위 로직
export class Pawn extends Piece {
    getAvailableMoves(board) {
        let moves = [];
        const direction = this.color === 'white' ? -1 : 1; // 백은 위로, 흑은 아래로 이동

        // 한 칸 전진
        const oneStepForward = { x: this.position.x, y: this.position.y + direction };
        if (this.isValidMove(oneStepForward, board) && !board[oneStepForward.y][oneStepForward.x]) {
            moves.push(oneStepForward);
        }

        // 첫 이동일 경우 두 칸 전진
        const startingRow = this.color === 'white' ? 6 : 1;
        const twoStepsForward = { x: this.position.x, y: this.position.y + 2 * direction };
        if (this.position.y === startingRow && !board[oneStepForward.y][oneStepForward.x] && !board[twoStepsForward.y][twoStepsForward.x]) {
            moves.push(twoStepsForward);
        }

        // 대각선 공격 (적 기물이 있는 경우만)
        const leftDiagonal = { x: this.position.x - 1, y: this.position.y + direction };
        const rightDiagonal = { x: this.position.x + 1, y: this.position.y + direction };

        if (this.isValidMove(leftDiagonal, board)) {
            const pieceAtLeftDiagonal = board[leftDiagonal.y]?.[leftDiagonal.x];
            if (pieceAtLeftDiagonal && pieceAtLeftDiagonal.color !== this.color) {
                moves.push(leftDiagonal);
            }
        }

        if (this.isValidMove(rightDiagonal, board)) {
            const pieceAtRightDiagonal = board[rightDiagonal.y]?.[rightDiagonal.x];
            if (pieceAtRightDiagonal && pieceAtRightDiagonal.color !== this.color) {
                moves.push(rightDiagonal);
            }
        }

        return moves;
    }
}

//퀸 이동 범위 로직
export class Queen extends Piece {
    getAvailableMoves(board) {
        let moves = [];

        // 룩의 상하좌우 이동
        // 오른쪽으로 이동
        for (let x = this.position.x + 1; x < 8; x++) {
            const newPosition = { x, y: this.position.y };
            if (!this.isValidMove(newPosition, board)) break;
            moves.push(newPosition);
            const pieceAtNewPosition = board[newPosition.y][newPosition.x];
            if (pieceAtNewPosition) {
                if (pieceAtNewPosition.color !== this.color) moves.push(newPosition);
                break;
            }
        }

        // 왼쪽으로 이동
        for (let x = this.position.x - 1; x >= 0; x--) {
            const newPosition = { x, y: this.position.y };
            if (!this.isValidMove(newPosition, board)) break;
            moves.push(newPosition);
            const pieceAtNewPosition = board[newPosition.y][newPosition.x];
            if (pieceAtNewPosition) {
                if (pieceAtNewPosition.color !== this.color) moves.push(newPosition);
                break;
            }
        }

        // 위로 이동
        for (let y = this.position.y - 1; y >= 0; y--) {
            const newPosition = { x: this.position.x, y };
            if (!this.isValidMove(newPosition, board)) break;
            moves.push(newPosition);
            const pieceAtNewPosition = board[newPosition.y][newPosition.x];
            if (pieceAtNewPosition) {
                if (pieceAtNewPosition.color !== this.color) moves.push(newPosition);
                break;
            }
        }

        // 아래로 이동
        for (let y = this.position.y + 1; y < 8; y++) {
            const newPosition = { x: this.position.x, y };
            if (!this.isValidMove(newPosition, board)) break;
            moves.push(newPosition);
            const pieceAtNewPosition = board[newPosition.y][newPosition.x];
            if (pieceAtNewPosition) {
                if (pieceAtNewPosition.color !== this.color) moves.push(newPosition);
                break;
            }
        }

        // 비숍의 대각선 이동
        // 오른쪽 위 대각선
        for (let x = this.position.x + 1, y = this.position.y - 1; x < 8 && y >= 0; x++, y--) {
            const newPosition = { x, y };
            if (!this.isValidMove(newPosition, board)) break;
            moves.push(newPosition);
            const pieceAtNewPosition = board[newPosition.y][newPosition.x];
            if (pieceAtNewPosition) {
                if (pieceAtNewPosition.color !== this.color) moves.push(newPosition);
                break;
            }
        }

        // 오른쪽 아래 대각선
        for (let x = this.position.x + 1, y = this.position.y + 1; x < 8 && y < 8; x++, y++) {
            const newPosition = { x, y };
            if (!this.isValidMove(newPosition, board)) break;
            moves.push(newPosition);
            const pieceAtNewPosition = board[newPosition.y][newPosition.x];
            if (pieceAtNewPosition) {
                if (pieceAtNewPosition.color !== this.color) moves.push(newPosition);
                break;
            }
        }

        // 왼쪽 위 대각선
        for (let x = this.position.x - 1, y = this.position.y - 1; x >= 0 && y >= 0; x--, y--) {
            const newPosition = { x, y };
            if (!this.isValidMove(newPosition, board)) break;
            moves.push(newPosition);
            const pieceAtNewPosition = board[newPosition.y][newPosition.x];
            if (pieceAtNewPosition) {
                if (pieceAtNewPosition.color !== this.color) moves.push(newPosition);
                break;
            }
        }

        // 왼쪽 아래 대각선
        for (let x = this.position.x - 1, y = this.position.y + 1; x >= 0 && y < 8; x--, y++) {
            const newPosition = { x, y };
            if (!this.isValidMove(newPosition, board)) break;
            moves.push(newPosition);
            const pieceAtNewPosition = board[newPosition.y][newPosition.x];
            if (pieceAtNewPosition) {
                if (pieceAtNewPosition.color !== this.color) moves.push(newPosition);
                break;
            }
        }

        return moves;
    }
}

//킹 이동 범위 로직
export class King extends Piece {
    getAvailableMoves(board) {
        let moves = [];
        const potentialMoves = [
            { x: this.position.x + 1, y: this.position.y },
            { x: this.position.x - 1, y: this.position.y },
            { x: this.position.x, y: this.position.y + 1 },
            { x: this.position.x, y: this.position.y - 1 },
            { x: this.position.x + 1, y: this.position.y + 1 },
            { x: this.position.x + 1, y: this.position.y - 1 },
            { x: this.position.x - 1, y: this.position.y + 1 },
            { x: this.position.x - 1, y: this.position.y - 1 }
        ];

        for (const move of potentialMoves) {
            if (this.isValidMove(move, board) && !this.isUnderAttack(move, board)) {
                moves.push(move);
            }
        }

        return moves;
    }

    // 킹이 공격당하는 위치인지 확인하는 함수 (가장 기본적인 구현 예시)
    isUnderAttack(position, board) {
        for (let row = 0; row < board.length; row++) {
            for (let col = 0; col < board[row].length; col++) {
                const piece = board[row][col];
                if (piece && piece.color !== this.color) {
                    const enemyMoves = piece.getAvailableMoves(board);
                    if (enemyMoves.some(move => move.x === position.x && move.y === position.y)) {
                        return true; // 킹이 공격당할 수 있는 위치라면 true 반환
                    }
                }
            }
        }
        return false;
    }
}


