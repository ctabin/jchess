
package ch.astorm.jchess.core;

/**
 * Represents the chess board.
 */
public class Board {
    private int nbRows;
    private int nbColumns;

    /**
     * Default number of columns on a regular chess board.
     */
    public static int DEFAULT_COLUMNS = 8;

    /**
     * Default number of rows on a regular chess board.
     */
    public static int DEFAULT_ROWS = 8;

    /**
     * Creates a new regular chess board.
     */
    public Board() {
        this(DEFAULT_ROWS, DEFAULT_COLUMNS);
    }

    /**
     * Creates a new board with the specified number of rows and columns.
     *
     * @param nbRows The number of rows.
     * @param nbColumns The number of columns;
     */
    public Board(int nbRows, int nbColumns) {
        if(nbRows<=0) { throw new IllegalArgumentException("Invalid number of rows: "+nbRows); }
        if(nbColumns<=0) { throw new IllegalArgumentException("Invalid number of columns: "+nbColumns); }

        this.nbRows = nbRows;
        this.nbColumns = nbColumns;
    }

    /**
     * Returns the number of rows.
     */
    public int getRowsCount() {
        return nbRows;
    }

    /**
     * Returns the number of columns.
     */
    public int getColumnsCount() {
        return nbColumns;
    }

    /**
     * Returns true if {@code coordinate} is valid according to this board.
     */
    public boolean isValid(Coordinate coordinate) {
        int row = coordinate.getRow();
        if(row<0 || row>=nbRows) { return false; }

        int column = coordinate.getColumn();
        if(column<0 || column>=nbColumns) { return false; }

        return true;
    }
}
