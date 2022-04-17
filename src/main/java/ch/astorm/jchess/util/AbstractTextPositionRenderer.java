
package ch.astorm.jchess.util;

import ch.astorm.jchess.core.Position;
import java.io.PrintStream;

/**
 * Provides default method for text position renderers.
 */
public abstract class AbstractTextPositionRenderer implements TextPositionRenderer {
    private String lineSeparator = System.lineSeparator();
    private final PrintStream out;
    
    protected AbstractTextPositionRenderer(PrintStream out) {
        this.out = out;
    }
    
    @Override
    public void render(Position position) {
        CharSequence seq = renderToString(position);
        out.print(seq);
        out.print(lineSeparator);
    }
    
    /**
     * Defines the line separator to use.
     * By default, the line separator is obtained with {@link System#lineSeparator()}.
     */
    public String getLineSeparator() { return lineSeparator; }
    public void setLineSeparator(String sep) { this.lineSeparator = sep; }
}
