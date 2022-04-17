
package ch.astorm.jchess.util;

import ch.astorm.jchess.core.Position;

/**
 * Represents a text renderer for a postion.
 */
public interface TextPositionRenderer extends PositionRenderer {
    
    /**
     * Returns the {@code position} represented in a {@code CharSequence}. 
     */
    CharSequence renderToString(Position position);
}
