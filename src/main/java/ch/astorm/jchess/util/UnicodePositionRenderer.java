package ch.astorm.jchess.util;

import ch.astorm.jchess.core.Color;
import ch.astorm.jchess.core.Moveable;
import ch.astorm.jchess.core.Position;
import ch.astorm.jchess.core.entities.*;

import java.io.PrintStream;

public class UnicodePositionRenderer implements PositionRenderer {
    private static final String separator = System.lineSeparator();
    private final PrintStream out;
    private final String emptyDarkCell;
    private final String emptyLightCell;

    /**
     * Creates a renderer to the specified {@code out} stream.
     */
    private UnicodePositionRenderer(PrintStream out, String emptyDarkCell, String emptyLightCell) {
        this.out = out;
        this.emptyDarkCell = emptyDarkCell;
        this.emptyLightCell = emptyLightCell;
    }

    public static void render(PrintStream out, Position position) {
        new UnicodePositionRenderer(out, "▧", " ").render(position);

    }
    public static void render(PrintStream out, Position position, String emptyDarkCell, String emptyLightCell) {
        new UnicodePositionRenderer(out, emptyDarkCell, emptyLightCell).render(position);
    }

    /**
     * Renders the given {@code position}.
     */
    @Override
    public void render(Position position) {
        out.println(renderToString(position));
    }

    private String renderToString(Position position) {
        StringBuilder sb = new StringBuilder(182);
        for (var i=7; i>=0; i--) {
            sb.append(8 - i).append("\t");
            for (var j=0; j<8; j++) {
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
            sb.append(separator);
            if (i>0) sb.append(separator);
        }
        sb.append("\ta\tb\tc\td\te\tf\tg\th");
        return sb.toString();
    }

    private static String getUnicode(Moveable moveable) {
        boolean isWhite = moveable.getColor() == Color.WHITE;
        if(moveable instanceof Rook)
            if (isWhite) return "♖"; else return "♜";
        if(moveable instanceof Knight)
            if (isWhite) return "♘"; else return "♞";
        if(moveable instanceof Bishop)
            if (isWhite) return "♗"; else return "♝";
        if(moveable instanceof King)
            if (isWhite) return "♔"; else return "♚";
        if(moveable instanceof Queen)
            if (isWhite) return "♕"; else return "♛";
        if(moveable instanceof Pawn)
            if (isWhite) return "♙"; else return "♟";
        throw new IllegalStateException();
    }
}
