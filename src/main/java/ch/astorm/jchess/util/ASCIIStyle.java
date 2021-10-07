
package ch.astorm.jchess.util;

import ch.astorm.jchess.core.Coordinate;
import ch.astorm.jchess.core.Moveable;
import ch.astorm.jchess.core.Position;

/**
 * Implements a style for the ASCII rendering of a {@link Position}.
 */
public interface ASCIIStyle {

    /**
     * Number of rows in cell.
     */
    public static final int NB_ROWS_PER_CELL = 5;

    /**
     * Number of columns in a cell.
     */
    public static final int NB_COLUMNS_PER_CELL = 9;

    /**
     * Renders the given {@code moveable} in the specified {@code cell}.
     * The cell size will be given by {@link #NB_ROWS_PER_CELL} and {@link #NB_COLUMNS_PER_CELL}.
     *
     * @param cell The cell.
     * @param cellCoordinate The coordinate.
     * @param moveable The entity.
     */
    void renderCell(char[][] cell, Coordinate cellCoordinate, Moveable moveable);

    /**
     * Returns the char to use for the border.
     */
    default char getBorderStyle() { return ':'; }

    /**
     * Returns the char to use as background for white cells.
     */
    default char getWhiteBackgroundStyle() { return ' '; }

    /**
     * Returns the char to use as background for black cells.
     */
    default char getBlackBackgroundStyle() { return ':'; }
}
