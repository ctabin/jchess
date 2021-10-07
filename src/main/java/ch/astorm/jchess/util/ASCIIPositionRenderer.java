
package ch.astorm.jchess.util;

import ch.astorm.jchess.core.Board;
import ch.astorm.jchess.core.Color;
import ch.astorm.jchess.core.Coordinate;
import ch.astorm.jchess.core.Moveable;
import ch.astorm.jchess.core.Position;
import java.io.PrintStream;
import java.util.function.Function;

/**
 * Simple utility class to render a {@code Position}.
 */
public class ASCIIPositionRenderer implements PositionRenderer {
    private static final String[] ROW_HEADER = new String[]
    {
        "     ",
        "  _  ",
        " (_) ",
        " (_) ",
        "     ",
        "     ",
        "  __ ",
        "   / ",
        "  /  ",
        "     ",
        "     ",
        "     ",
        "  /  ",
        " (_) ",
        "     ",
        "     ",
        "  _  ",
        " |_  ",
        "  _) ",
        "     ",
        "     ",
        "   . ",
        "  /| ",
        " '-| ",
        "     ",
        "     ",
        "  _  ",
        "  _) ",
        "  _) ",
        "     ",
        "     ",
        "  _  ",
        "   ) ",
        "  /_ ",
        "     ",
        "     ",
        "     ",
        "  /| ",
        "   | ",
        "     ",
    };

    private PrintStream out;
    private ASCIIStyle style;

    /**
     * Creates a renderer to the specified {@code out} stream.
     */
    public ASCIIPositionRenderer(PrintStream out, ASCIIStyle style) {
        this.out = out;
        this.style = style;
    }

    /**
     * Renders the given {@code position} with a {@link DefaultASCIIStyle}.
     */
    public static void render(PrintStream out, Position position) {
        new ASCIIPositionRenderer(out, new DefaultASCIIStyle()).render(position);
    }

    @Override
    public void render(Position position) {
        Board board = position.getBoard();

        char whiteBackgroundStyle = style.getWhiteBackgroundStyle();
        char blackBackgroundStyle = style.getBlackBackgroundStyle();
        char borderStyle = style.getBorderStyle();

        int nbRows = board.getRowsCount();
        int nbColumns = board.getColumnsCount();
        int nbTotalRows = nbRows*ASCIIStyle.NB_ROWS_PER_CELL;
        int nbTotalColumns = nbColumns*ASCIIStyle.NB_COLUMNS_PER_CELL;
        char[][] assciiBoard = new char[nbTotalRows][nbTotalColumns];

        for(int row=0 ; row<nbRows ; ++row) {
            for(int col=0 ; col<nbColumns ; ++col) {
                Coordinate coordinate = new Coordinate(row, col);
                char filling = board.getCellColor(coordinate)==Color.WHITE ? whiteBackgroundStyle : blackBackgroundStyle;
                
                char[][] cell = new char[ASCIIStyle.NB_ROWS_PER_CELL][ASCIIStyle.NB_COLUMNS_PER_CELL];
                for(int ti=0 ; ti<ASCIIStyle.NB_ROWS_PER_CELL ; ++ti) {
                    for(int tc=0 ; tc<ASCIIStyle.NB_COLUMNS_PER_CELL ; ++tc) {
                        cell[ti][tc] = filling;
                    }
                }

                Moveable moveable = position.get(coordinate);
                if(moveable!=null) { style.renderCell(cell, coordinate, moveable); }

                int rowOffset = (nbRows - row - 1)*ASCIIStyle.NB_ROWS_PER_CELL;
                int colOffset = col*ASCIIStyle.NB_COLUMNS_PER_CELL;
                for(int ti=0 ; ti<ASCIIStyle.NB_ROWS_PER_CELL ; ++ti) {
                    for(int tc=0 ; tc<ASCIIStyle.NB_COLUMNS_PER_CELL ; ++tc) {
                        assciiBoard[rowOffset+ti][colOffset+tc] = cell[ti][tc];
                    }
                }
            }
        }

        //first border
        out.print("     ");
        printRow(nbTotalColumns+2, i -> borderStyle);
        out.println();

        //whole board
        for(int r=0 ; r<nbTotalRows ; ++r) {
            String headerRow = ROW_HEADER[r];
            for(int hr=0 ; hr<headerRow.length() ; ++hr) { out.print(headerRow.charAt(hr)); }

            out.print(borderStyle);

            char[] row = assciiBoard[r];
            printRow(nbTotalColumns, colIndex -> row[colIndex]);
            
            out.print(borderStyle);
            out.println();
        }

        //last border
        out.print("     ");
        printRow(nbTotalColumns+2, i -> borderStyle);
        out.println();

        out.println("                   _        _        _        __       __       _              ");
        out.println("         /\\       |_)      /        | \\      |_       |_       /        |_|    ");
        out.println("        /--\\      |_)      \\_       |_/      |__      |        \\_?      | |    ");
        out.println("                                                                               ");
        out.println();
    }

    private void printRow(int nbColumns, Function<Integer, Character> content) {
        for(int i=0 ; i<nbColumns ; ++i) {
            out.print(content.apply(i));
        }
    }
}
