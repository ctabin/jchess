package ch.astorm.jchess.util;

import ch.astorm.jchess.core.Color;
import ch.astorm.jchess.core.Moveable;
import ch.astorm.jchess.core.Position;
import ch.astorm.jchess.core.entities.*;

import java.io.PrintStream;

public class UnicodePositionRenderer implements PositionRenderer {
    private final PrintStream out;
    private final String emptyCell;

    /**
     * Creates a renderer to the specified {@code out} stream.
     */
    private UnicodePositionRenderer(PrintStream out, String emptyCell) {
        this.out = out;
        this.emptyCell = emptyCell;
    }

    public static void render(PrintStream out, Position position) {
        new UnicodePositionRenderer(out, "").render(position);
    }

    public static void render(PrintStream out, Position position, String emptyCell) {
        new UnicodePositionRenderer(out, emptyCell).render(position);
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

    @Override
    public void render(Position position) {
        for (var i=7; i>=0; i--) {
            out.print((8-i) + "\t");
            for (var j=0; j<8; j++) {
                Moveable moveable =  position.get(i, j);
                if (moveable != null) {
                    String unicodeMovable = getUnicode(moveable);
                    out.print(unicodeMovable + "\t");
                } else {
                    out.print(emptyCell + "\t");
                }
            }
            out.println();
            if (i>0) out.println();
        }
        out.println("\ta\tb\tc\td\te\tf\tg\th");
    }
}
