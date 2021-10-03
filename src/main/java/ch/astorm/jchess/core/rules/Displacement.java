
package ch.astorm.jchess.core.rules;

import ch.astorm.jchess.core.Coordinate;
import ch.astorm.jchess.core.Moveable;

/**
 * Represents an entity displacement.
 */
public class Displacement {
    private Moveable moveable;
    private Coordinate oldLocation;
    private Coordinate newLocation;

    /**
     * Creates a new displacement.
     *
     * @param moveable The {@link Moveable} entity.
     * @param oldPosition The current position of the {@code moveable} entity.
     * @param newPosition The new position of the {@code moveable} entity.
     */
    public Displacement(Moveable moveable, Coordinate oldPosition, Coordinate newPosition) {
        this.moveable = moveable;
        this.oldLocation = oldPosition;
        this.newLocation = newPosition;
    }

    /**
     * Returns the {@link Moveable} entity.
     */
    public Moveable getMoveable() {
        return moveable;
    }

    /**
     * Returns the old location of the {@link Moveable} entity.
     * This is its location before the move.
     */
    public Coordinate getOldLocation() {
        return oldLocation;
    }

    /**
     * Returns the new location of the {@link Moveable} entity.
     * This will be its location after the move.
     */
    public Coordinate getNewLocation() {
        return newLocation;
    }
}
