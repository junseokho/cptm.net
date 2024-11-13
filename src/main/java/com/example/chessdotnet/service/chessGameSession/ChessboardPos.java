package com.example.chessdotnet.service.chessGameSession;


/**
 * class to represent coordinate of chessboard.
 * Be careful, Instance of this always can have invalid value.
 */
public class ChessboardPos {
    /**
     * row index of board. (valid value in chessboard is between 0-7)
     */
    public int row;

    /**
     * cow index of board. (valid value in chessboard is between 0-7)
     */
    public int col;

    /**
     * check if this ChessboardPos instance is legal.
     * @return true if this ChessboardPos has legal values, in boundary 0~7.
     */
    public boolean isValid() {
        return (0 <= row && row <= 7 && 0 <= col && col <= 7);
    }

    /**
     * check if given position is legal.
     * @return true if given position has legal values, in boundary 0~7.
     */
    public static boolean isValid(int row, int col) {
        return (0 <= row && row <= 7 && 0 <= col && col <= 7);
    }


    /**
     * construct by clone another ChessboardPos instance.
     * @param pos another ChessboardPos instance to clone
     */
    public ChessboardPos(ChessboardPos pos) {
        row = pos.row;
        col = pos.col;
    }

    /**
     * default constructor
     * @param row row index of board.
     * @param col cow index of board.
     */
    public ChessboardPos(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * check if this has same values to `operand`.
     * @param operand another ChessboardPos to compare
     * @return true if row and col are same each other.
     */
    @Override
    public boolean equals(Object operand) {
        if (!(operand instanceof ChessboardPos pos))
            return false;
        return (this.row == pos.row && this.col == pos.col);
    }

    /**
     * add values of `operand` onto `this`
     * @param operand operand to add
     * @return itself (`this`)
     */
    public ChessboardPos add(ChessboardPos operand) {
        this.row += operand.row;
        this.col += operand.col;
        return this;
    }

    /**
     * Return new ChessboardPos, having sum of values of them. Simply put, it returns value of `lhs + rhs`
     * There is no side effect.
     * @param lhs left hand side of operator
     * @param rhs right hand side of operator
     * @return new ChessboardPos, having sum of values of them.
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
     * @param lhs left hand side of operator
     * @param rhs right hand side of operator
     * @return new ChessboardPos, having sum of values of them.
     */
    public static ChessboardPos sub(ChessboardPos lhs, ChessboardPos rhs) {
        ChessboardPos ret = new ChessboardPos(lhs);
        ret.row -= rhs.row;
        ret.col -= rhs.col;
        return ret;
    }
}
