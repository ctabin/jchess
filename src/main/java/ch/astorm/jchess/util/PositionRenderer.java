
package ch.astorm.jchess.util;

import ch.astorm.jchess.core.Position;

/**
 * Represents a {@link Position} renderer.
 */
public interface PositionRenderer {

    /**
     * Renders the given {@code position}.
     */
    void render(Position position);
}
