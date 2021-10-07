
package ch.astorm.jchess.util;

import ch.astorm.jchess.JChessGame;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class ASCIIPositionRendererTest {

    @Test
    public void testRendering() {
        JChessGame game = JChessGame.newGame();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try(PrintStream stream = new PrintStream(baos, true, StandardCharsets.UTF_8)) {
            ASCIIPositionRenderer.render(stream, game.getPosition());
        }

        String str = new String(baos.toByteArray(), StandardCharsets.UTF_8);
        assertEquals(3681, str.length());
    }
}
