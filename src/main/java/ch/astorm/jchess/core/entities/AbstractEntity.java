
package ch.astorm.jchess.core.entities;

import ch.astorm.jchess.core.Color;
import ch.astorm.jchess.core.Moveable;

/**
 * Generic methods of entities.
 */
public class AbstractEntity implements Moveable {
    private final Color color;

    protected AbstractEntity(Color color) {
        this.color = color;
    }

    @Override
    public Color getColor() {
        return color;
    }
}
