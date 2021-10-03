
package ch.astorm.jchess.core;

/**
 * A coordinate on the {@link Board}.
 */
public class Coordinate {
    private int row;
    private int column;

    /**
     * Creates a new {@code Coordinate}.
     *
     * @param row The row index (zero-based).
     * @param column The column index (zero-based).
     */
    public Coordinate(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * Returns the row.
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the column.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Returns a new {@code Coordinate} with the specified increments, starting from
     * this coordinate. The current instance is left untouched.
     *
     * @param rowIncr The row increment.
     * @param columnIncr The column increment.
     * @return A new {@code Coordinate} instance.
     */
    public Coordinate to(int rowIncr, int columnIncr) {
        return new Coordinate(row+rowIncr, column+columnIncr);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.row;
        hash = 79 * hash + this.column;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Coordinate other = (Coordinate) obj;
        if (this.row != other.row) {
            return false;
        }
        if (this.column != other.column) {
            return false;
        }
        return true;
    }
}
