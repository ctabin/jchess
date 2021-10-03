
package ch.astorm.jchess.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class BoardTest {
    @Test
    public void testCustomBoard() {
        assertThrows(IllegalArgumentException.class, () -> new Board(-1, 10));
        assertThrows(IllegalArgumentException.class, () -> new Board(10, -1));

        Board custom = new Board(10, 15);
        assertEquals(10, custom.getRowsCount());
        assertEquals(15, custom.getColumnsCount());
    }
}
