
package ch.astorm.jchess.util;

import ch.astorm.jchess.core.Color;
import ch.astorm.jchess.core.Moveable;
import ch.astorm.jchess.core.Position;
import ch.astorm.jchess.core.entities.Bishop;
import ch.astorm.jchess.core.entities.King;
import ch.astorm.jchess.core.entities.Knight;
import ch.astorm.jchess.core.entities.Pawn;
import ch.astorm.jchess.core.entities.Queen;
import ch.astorm.jchess.core.entities.Rook;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Simple utility class to render a {@code Position}.
 */
public class PositionRenderer {

    /**
     * Mapping of each {@link Moveable} subclass to a single char for printing.
     */
    public static final Map<Class<? extends Moveable>, Function<Color, String>> MOVEABLE_RENDER = new HashMap<>(){{
        put(Pawn.class, c -> c==Color.WHITE ? "î" : "!");
        put(Rook.class, c -> c==Color.WHITE ? "R" : "r");
        put(Bishop.class, c -> c==Color.WHITE ? "B" : "b");
        put(Knight.class, c -> c==Color.WHITE ? "N" : "n");
        put(Queen.class, c -> c==Color.WHITE ? "Q" : "q");
        put(King.class, c -> c==Color.WHITE ? "K" : "k");
    }};

    /**
     * Mapping for the column letters.
     */
    public static final Map<Integer, String> COLUMN_LETTERS = new HashMap<>(){{
        put(0, "a");
        put(1, "b");
        put(2, "c");
        put(3, "d");
        put(4, "e");
        put(5, "f");
        put(6, "g");
        put(7, "h");
    }};

    /**
     * Renders the given {@code position} in a String.
     * Here is a result example:
     * <pre>
     *        |---|---|---|---|---|---|---|---|
     *      8 | r | n | b | q | k | b | n | r |
     *        |---|---|---|---|---|---|---|---|
     *      7 | ! | ! | ! | ! | ! | ! | ! | ! |
     *        |---|---|---|---|---|---|---|---|
     *      6 |   |   |   |   |   |   |   |   |
     *        |---|---|---|---|---|---|---|---|
     *      5 |   |   |   |   |   |   |   |   |
     *        |---|---|---|---|---|---|---|---|
     *      4 |   |   |   |   |   |   |   |   |
     *        |---|---|---|---|---|---|---|---|
     *      3 |   |   |   |   |   |   |   |   |
     *        |---|---|---|---|---|---|---|---|
     *      2 | î | î | î | î | î | î | î | î |
     *        |---|---|---|---|---|---|---|---|
     *      1 | R | N | B | Q | K | B | N | R |
     *        |---|---|---|---|---|---|---|---|
     *          a   b   c   d   e   f   g   h
     * </pre>
     */
    public static String render(Position position) {
        StringBuilder builder = new StringBuilder(1024);
        for(int i=7 ; i>=0 ; --i) {
            if(i==7) {
                for(int k=0 ; k<8 ; ++k) {
                    if(k==0) { builder.append("  |"); }
                    builder.append("---|");
                }
                builder.append("\n");
            }

            for(int k=0 ; k<8 ; ++k) {
                if(k==0) { builder.append(i+1).append(" |"); }
                Moveable m = position.get(i, k);
                if(m==null) { builder.append("   "); }
                else { builder.append(" ").append(MOVEABLE_RENDER.get(m.getClass()).apply(m.getColor())).append(" "); }
                builder.append("|");
            }
            builder.append("\n");

            if(i>0) {
                for(int k=0 ; k<8 ; ++k) {
                    if(k==0) { builder.append("  |"); }
                    builder.append("---|");
                }
                builder.append("\n");
            } else {
                for(int k=0 ; k<8 ; ++k) {
                    if(k==0) { builder.append("  |"); }
                    builder.append("---|");
                }
                builder.append("\n");

                for(int k=0 ; k<8 ; ++k) {
                    if(k==0) { builder.append("   "); }
                    
                    String str = COLUMN_LETTERS.get(k);
                    builder.append(" ").append(str).append(" ");
                    builder.append(" ");
                }
                builder.append("\n");
            }
        }
        return builder.toString();
    }
}
