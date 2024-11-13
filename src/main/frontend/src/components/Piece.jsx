/**
 * Represents a chess piece.
 * @class
 */
export class Piece {
    /**
     * Creates a chess piece.
     * @param {string} name - The name of the piece.
     * @param {Object} position - The current position of the piece.
     * @param {number} position.row - The row position.
     * @param {number} position.col - The column position.
     * @param {string} color - The color of the piece ('white' or 'black').
     * @param {string} image - The image URL for the piece.
     */
    constructor(name, position, color, image) {
        this.name = name;
        this.position = position;
        this.color = color;
        this.image = image;
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
        const pieceAtDestination = board[position.col][position.row];
        return !(pieceAtDestination);
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

                const pieceAtNewPosition = board[col][row];
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
            { row: 1, col: 0 },  // right
            { row: -1, col: 0 }, // left
            { row: 0, col: 1 },  // down
            { row: 0, col: -1 }  // up
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
     * Gets the available moves for the Pawn.
     * @param {Array} board - The current state of the board.
     * @returns {Array} - A list of valid move positions.
     */
    getAvailableMoves(board) {
        let moves = [];
        const direction = this.color === 'white' ? 1 : -1;

        const oneStepForward = { row: this.position.row, col: this.position.col + direction };
        if (this.isValidMove(oneStepForward, board) && !board[oneStepForward.col][oneStepForward.row]) {
            moves.push(oneStepForward);
        }

        const startingRow = this.color === 'white' ? 1 : 6;
        const twoStepsForward = { row: this.position.row, col: this.position.col + 2 * direction };
        if (this.position.col === startingRow && !board[oneStepForward.col][oneStepForward.row] && !board[twoStepsForward.col][twoStepsForward.row]) {
            moves.push(twoStepsForward);
        }

        const leftDiagonal = { row: this.position.row - 1, col: this.position.col + direction };
        const rightDiagonal = { row: this.position.row + 1, col: this.position.col + direction };

        if (this.isValidMove(leftDiagonal, board)) {
            const pieceAtLeftDiagonal = board[leftDiagonal.col]?.[leftDiagonal.row];
            if (pieceAtLeftDiagonal && pieceAtLeftDiagonal.color !== this.color) {
                moves.push(leftDiagonal);
            }
        }

        if (this.isValidMove(rightDiagonal, board)) {
            const pieceAtRightDiagonal = board[rightDiagonal.col]?.[rightDiagonal.row];
            if (pieceAtRightDiagonal && pieceAtRightDiagonal.color !== this.color) {
                moves.push(rightDiagonal);
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
     * @returns {Array} - A list of valid move positions.
     */
    getAvailableMoves(board) {
        let moves = [];
        const potentialMoves = [
            { row: this.position.row + 1, col: this.position.col },
            { row: this.position.row - 1, col: this.position.col },
            { row: this.position.row, col: this.position.col + 1 },
            { row: this.position.row, col: this.position.col - 1 },
            { row: this.position.row + 1, col: this.position.col + 1 },
            { row: this.position.row + 1, col: this.position.col - 1 },
            { row: this.position.row - 1, col: this.position.col + 1 },
            { row: this.position.row - 1, col: this.position.col - 1 }
        ];

        for (const move of potentialMoves) {
            if (this.isValidMove(move, board) && !this.isUnderAttack(move, board)) {
                moves.push(move);
            }
        }


        // Castling logic
        if (!this.hasMoved && !this.isUnderAttack(this.position, board)) {
            // Check for Kingside Castling (right side)
            const rookKingSide = board[this.position.col + 3]?.[this.position.row];
            if (rookKingSide instanceof Rook && !rookKingSide.hasMoved) {
                if (!board[this.position.col + 1]?.[this.position.row] &&
                    !board[this.position.col + 2]?.[this.position.row] &&
                    !this.isUnderAttack({ row: this.position.row, col: this.position.col + 1 }, board) &&
                    !this.isUnderAttack({ row: this.position.row, col: this.position.col + 2 }, board)) {
                    moves.push({ row: this.position.row, col: this.position.col + 2 });
                }
            }

            // Check for Queenside Castling (left side)
            const rookQueenSide = board[this.position.col - 4]?.[this.position.row];
            if (rookQueenSide instanceof Rook && !rookQueenSide.hasMoved) {
                if (!board[this.position.col - 1]?.[this.position.row] &&
                    !board[this.position.col - 2]?.[this.position.row] &&
                    !board[this.position.col - 3]?.[this.position.row] &&
                    !this.isUnderAttack({ row: this.position.row, col: this.position.col - 1 }, board) &&
                    !this.isUnderAttack({ row: this.position.row, col: this.position.col - 2 }, board)) {
                    moves.push({ row: this.position.row, col: this.position.col - 2 });
                }
            }
        }

        return moves;
    }

    /**
     * Checks if the given position is under attack by enemy pieces.
     * @param {Object} position - The position to check.
     * @param {number} position.row - The row to check.
     * @param {number} position.col - The column to check.
     * @param {Array} board - The current board state.
     * @returns {boolean} - True if the position is under attack.
     */
    isUnderAttack(position, board) {
        const directions = [
            { row: 1, col: 0 },  { row: -1, col: 0 },
            { row: 0, col: 1 },  { row: 0, col: -1 },
            { row: 1, col: 1 },  { row: 1, col: -1 },
            { row: -1, col: 1 }, { row: -1, col: -1 }
        ];

        // Check for enemy rooks, queens, and bishops in all directions
        for (const direction of directions) {
            let row = position.row;
            let col = position.col;
            while (true) {
                row += direction.row;
                col += direction.col;
                if (row < 0 || row > 7 || col < 0 || col > 7) break;
                const piece = board[col][row];
                if (piece) {
                    if (piece.color !== this.color && (piece instanceof Rook || piece instanceof Queen || piece instanceof Bishop)) {
                        return true;
                    }
                    break;
                }
            }
        }

        // Check for enemy knights
        const knightMoves = [
            { row: 2, col: 1 }, { row: 2, col: -1 },
            { row: -2, col: 1 }, { row: -2, col: -1 },
            { row: 1, col: 2 }, { row: 1, col: -2 },
            { row: -1, col: 2 }, { row: -1, col: -2 }
        ];

        for (const move of knightMoves) {
            const newRow = position.row + move.row;
            const newCol = position.col + move.col;
            if (newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8) {
                const piece = board[newCol]?.[newRow];
                if (piece && piece.color !== this.color && piece instanceof Knight) {
                    return true;
                }
            }
        }

        return false;
    }
}
