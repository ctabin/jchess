
package ch.astorm.jchess.core.rules;

import ch.astorm.jchess.core.Move;
import ch.astorm.jchess.core.Coordinate;
import ch.astorm.jchess.core.Moveable;
import ch.astorm.jchess.core.Position;
import java.util.List;

/**
 * Rule for a displacement of a {@link Moveable}.
 */
public interface DisplacementRule<T extends Moveable> {

    /**
     * Returns all the available moves of the {@code moveable} entity, given its
     * current {@code position} and the actual {@code board}.
     * <p>Note that this method does not check the validity of the move (capturing the
     * opponent's King or making its King in check). However, this method should check if other
     * entities are on blocking their way and includes the coordinate if the latter can be captured.</p>
     *
     * @param position The current {@link Position}.
     * @param location The current location of the {@code moveable} entity.
     * @param moveable The {@link Moveable} entity.
     * @return A list of available moves or an empty list if there is none.
     */
    List<Move> getAvailableMoves(Position position, Coordinate location, T moveable);

    /**
     * Returns true if the {@code moveable} entity can move to the specified {@code target}
     * on the given {@code position}.
     *
     * @param position The {@link Position}.
     * @param location The current location of the {@code moveable} entity.
     * @param moveable The {@link Moveable} entity.
     * @param target The target {@link Coordinate}.
     * @return True if the {@code target} is accessible.
     */
    boolean canAccess(Position position, Coordinate location, T moveable, Coordinate target);
}
