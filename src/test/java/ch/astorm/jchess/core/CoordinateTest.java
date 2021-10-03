
package ch.astorm.jchess.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CoordinateTest {

    @Test
    public void testProperties() {
        Coordinate c = new Coordinate(0, 1);
        assertEquals(0, c.getRow());
        assertEquals(1, c.getColumn());
    }

    @Test
    public void testEquality() {
        Coordinate a = new Coordinate(0, 1);
        Coordinate b = new Coordinate(0, 1);
        Coordinate c = new Coordinate(1, 1);

        assertTrue(a.equals(a));
        assertTrue(a.equals(b));
        assertTrue(b.equals(a));
        assertFalse(a.equals(c));
        assertFalse(c.equals(a));
        assertFalse(a.equals(new Object()));
        assertFalse(a.equals(null));
    }

    @Test
    public void testHashCode() {
        Coordinate a = new Coordinate(0, 1);
        Coordinate b = new Coordinate(0, 1);
        Coordinate c = new Coordinate(1, 1);

        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a.hashCode(), c.hashCode());
    }
}
