
package ch.astorm.jchess.core;

/**
 * Available colors for players.
 */
public enum Color {
    WHITE,
    BLACK;

    /**
     * Returns the opposite color.
     */
    public Color opposite() {
        return this==WHITE ? BLACK : WHITE;
    }
}
