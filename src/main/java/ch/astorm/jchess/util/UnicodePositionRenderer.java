package ch.astorm.jchess.util;

import ch.astorm.jchess.core.Color;
import ch.astorm.jchess.core.Moveable;
import ch.astorm.jchess.core.Position;
import ch.astorm.jchess.core.entities.*;

import java.io.PrintStream;

public class UnicodePositionRenderer extends AbstractTextPositionRenderer {
    private final String emptyDarkCell;
    private final String emptyLightCell;

    /**
     * Creates a renderer to the specified {@code out} stream.
     */
    private UnicodePositionRenderer(PrintStream out, String emptyDarkCell, String emptyLightCell) {
        super(out);
        this.emptyDarkCell = emptyDarkCell;
        this.emptyLightCell = emptyLightCell;
    }

    public static void render(PrintStream out, Position position) {
        render(out, position, " ", " ");
    }

    public static void render(PrintStream out, Position position, String emptyDarkCell, String emptyLightCell) {
        new UnicodePositionRenderer(out, emptyDarkCell, emptyLightCell).render(position);
    }

    @Override
    public CharSequence renderToString(Position position) {
        StringBuilder sb = new StringBuilder(182);
        int nbRows = position.getBoard().getRowsCount();
        int nbColumns = position.getBoard().getColumnsCount();
        for (var i=nbRows-1; i>=0; i--) {
            sb.append(i+1).append("\t");
            for (var j=0; j<nbColumns; j++) {
                boolean isDark = (i % 2 == 0) == (j % 2 == 0);
                String emptyCell = isDark ? emptyDarkCell : emptyLightCell;
                Moveable moveable =  position.get(i, j);
                if (moveable != null) {
                    String unicodeMovable = getUnicode(moveable);
                    sb.append(unicodeMovable).append("\t");
                } else {
                    sb.append(emptyCell).append("\t");
                }
            }
            sb.append(super.getLineSeparator());
            if (i>0) sb.append(super.getLineSeparator());
        }
        sb.append(" \ta\tb\tc\td\te\tf\tg\th");
        return sb;
    }

    private static String getUnicode(Moveable moveable) {
        boolean isWhite = moveable.getColor() == Color.WHITE;
        if(moveable instanceof Rook)
           return isWhite ? "♖" : "♜";
        if(moveable instanceof Knight)
            return isWhite ? "♘" : "♞";
        if(moveable instanceof Bishop)
            return isWhite ? "♗" : "♝";
        if(moveable instanceof King)
            return isWhite ? "♔" : "♚";
        if(moveable instanceof Queen)
            return isWhite ? "♕" : "♛";
        if(moveable instanceof Pawn)
            return isWhite ? "♙" : "♟";
        throw new IllegalArgumentException("Unhandled moveable: "+moveable);
    }
}
