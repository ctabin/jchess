
package ch.astorm.jchess.core.rules;

import ch.astorm.jchess.core.Board;
import ch.astorm.jchess.core.Color;
import ch.astorm.jchess.core.Coordinate;
import ch.astorm.jchess.core.Moveable;
import ch.astorm.jchess.core.Position;
import ch.astorm.jchess.core.Move;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Simple generic displacement rules.
 */
public class SimpleDisplacementRule<T extends Moveable> implements DisplacementRule<T> {
    private final List<LookupDirection> lookups = new ArrayList<>(8);

    private static class LookupDirection {
       int rowIncrement;
       int columnIncrement;
       int limit;
    }

    /**
     * Creates a new {@code SImpleDisplacementRule} to lookup in any directions.
     * This would be typically used for the quees with a limit of -1 or the kings
     * with a limit of 1.
     *
     * @param limit The maximum number of times to repeat the lookup or -1 if no limit.
     * @return A new rule instance.
     */
    public static SimpleDisplacementRule<Moveable> anyDirections(int limit) {
        SimpleDisplacementRule<Moveable> sdr = new SimpleDisplacementRule<>();
        sdr.withVerticalHorizontalLookups(limit);
        sdr.withDiagonalLookups(limit);
        return sdr;
    }

    /**
     * Adds a custom lookup with the specified increments.
     *
     * @param rowIncrement The row increment.
     * @param columnIncrement The column increment.
     * @param limit The maximum number of times to repeat or -1 if no limit.
     * @return This rule (allows chaining).
     * @see #withVerticalHorizontalLookups(int)
     * @see #withDiagonalLookups(int)
     */
    public SimpleDisplacementRule<T> withCustomLookup(int rowIncrement, int columnIncrement, int limit) {
        LookupDirection ld = new LookupDirection();
        ld.rowIncrement = rowIncrement;
        ld.columnIncrement = columnIncrement;
        ld.limit = limit;
        lookups.add(ld);

        return this;
    }

    /**
     * Adds vertical and horizontal lookups.
     * For instance, this method is used for the rooks with a {@code limit} of -1.
     *
     * @param limit The maximum number of times to repeat or -1 if no limit.
     * @return This rule (allows chaining).
     */
    public SimpleDisplacementRule<T> withVerticalHorizontalLookups(int limit) {
        withCustomLookup(0, 1, limit);
        withCustomLookup(0, -1, limit);
        withCustomLookup(1, 0, limit);
        withCustomLookup(-1, 0, limit);
        return this;
    }

    /**
     * Adds diagonal lookups.
     * For instance, this method is used for the bishops with a {@code limit} of -1.
     *
     * @param limit The maximum number of times to repeat or -1 if no limit.
     * @return This rule (allows chaining).
     */
    public SimpleDisplacementRule<T> withDiagonalLookups(int limit) {
        withCustomLookup(1, 1, limit);
        withCustomLookup(1, -1, limit);
        withCustomLookup(-1, 1, limit);
        withCustomLookup(-1, -1, limit);
        return this;
    }

    @Override
    public List<Move> getAvailableMoves(Position position, Coordinate location, T moveable) {
        List<Move> moves = new ArrayList<>();
        computeMoves(position, location, moveable, move -> {
            moves.add(move);
            return null;
        }, null);
        return moves;
    }

    @Override
    public boolean canAccess(Position position, Coordinate location, T moveable, Coordinate target) {
        return computeMoves(position, location, moveable, move -> {
            if(move.getDisplacement().getNewLocation().equals(target)) { return true; }
            return null;
        }, false);
    }

    private <T> T computeMoves(Position position, Coordinate location, Moveable moveable, Function<Move, T> moveHandler, T defaultValue) {
        Board board = position.getBoard();
        Color color = moveable.getColor();
        for(LookupDirection lookup : lookups) {
            int lookupCounter = 0;
            Coordinate newLocation = location.to(lookup.rowIncrement, lookup.columnIncrement);
            while((lookup.limit<0 || lookupCounter<lookup.limit) && board.isValid(newLocation)) {
                Displacement mainDisplacement = new Displacement(moveable, location, newLocation);
                Moveable atPosition = position.get(newLocation);
                if(atPosition!=null) {
                    if(atPosition.getColor()!=color) {
                        T result = moveHandler.apply(new Move(position, mainDisplacement, atPosition));
                        if(result!=null) { return result; }
                    }
                    break;
                }

                T result = moveHandler.apply(new Move(position, mainDisplacement));
                if(result!=null) { return result; }

                newLocation = newLocation.to(lookup.rowIncrement, lookup.columnIncrement);
                ++lookupCounter;
            }
        }

        return defaultValue;
    }
}
