package com.example.chessdotnet.service.chessGameSession;


/**
 * Class to represent coordinate of chessboard.
 * Be careful, Instance of this always can have invalid value.
 */
public class ChessboardPos {
    /**
     * Row index of board. (valid value in chessboard is between 0-7)
     */
    public int row;

    /**
     * Cow index of board. (valid value in chessboard is between 0-7)
     */
    public int col;

    /**
     * Check if this ChessboardPos instance is legal.
     * @return True if this ChessboardPos has legal values, in boundary 0~7.
     */
    public boolean isValid() {
        return (0 <= row && row <= 7 && 0 <= col && col <= 7);
    }

    /**
     * Check if given position is legal.
     * @return True if given position has legal values, in boundary 0~7.
     */
    public static boolean isValid(int row, int col) {
        return (0 <= row && row <= 7 && 0 <= col && col <= 7);
    }


    /**
     * Construct by clone another ChessboardPos instance.
     * @param pos Another ChessboardPos instance to clone
     */
    public ChessboardPos(ChessboardPos pos) {
        row = pos.row;
        col = pos.col;
    }

    /**
     * Default constructor
     * @param row Row index of board.
     * @param col Cow index of board.
     */
    public ChessboardPos(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Check if this has same values to `operand`.
     * @param operand Another ChessboardPos to compare
     * @return True if row and col are same each other.
     */
    @Override
    public boolean equals(Object operand) {
        if (!(operand instanceof ChessboardPos pos))
            return false;
        return (this.row == pos.row && this.col == pos.col);
    }

    /**
     * Add values of `operand` onto `this`
     * @param operand Operand to add
     * @return Itself (`this`)
     */
    public ChessboardPos add(ChessboardPos operand) {
        this.row += operand.row;
        this.col += operand.col;
        return this;
    }

    /**
     * Return new ChessboardPos, having sum of values of them. Simply put, it returns value of `lhs + rhs`
     * There is no side effect.
     * @param lhs Left hand side of operator
     * @param rhs Right hand side of operator
     * @return New ChessboardPos, having sum of values of them.
     */
    public static ChessboardPos add(ChessboardPos lhs, ChessboardPos rhs) {
        ChessboardPos ret = new ChessboardPos(lhs);
        ret.row += rhs.row;
        ret.col += rhs.col;
        return ret;
    }

    /**
     * Return new ChessboardPos, having difference of values of them. Simply put, it returns value of `lhs - rhs`
     * There is no side effect.
     * @param lhs Left hand side of operator
     * @param rhs Right hand side of operator
     * @return New ChessboardPos, having sum of values of them.
     */
    public static ChessboardPos sub(ChessboardPos lhs, ChessboardPos rhs) {
        ChessboardPos ret = new ChessboardPos(lhs);
        ret.row -= rhs.row;
        ret.col -= rhs.col;
        return ret;
    }
}
