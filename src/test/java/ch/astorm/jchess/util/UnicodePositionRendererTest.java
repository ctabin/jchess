
package ch.astorm.jchess.util;

import ch.astorm.jchess.JChessGame;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnicodePositionRendererTest {
    private final String separator = System.lineSeparator();
    private final String expectedOutput = "" +
            "1\t♜\t♞\t♝\t♛\t♚\t♝\t♞\t♜\t" + separator + separator +
            "2\t♟\t♟\t♟\t♟\t♟\t♟\t♟\t♟\t" + separator + separator +
            "3\t \t*\t \t*\t \t*\t \t*\t" + separator + separator +
            "4\t*\t \t*\t \t*\t \t*\t \t" + separator + separator +
            "5\t \t*\t \t*\t \t*\t \t*\t" + separator + separator +
            "6\t*\t \t*\t \t*\t \t*\t \t" + separator + separator +
            "7\t♙\t♙\t♙\t♙\t♙\t♙\t♙\t♙\t" + separator + separator +
            "8\t♖\t♘\t♗\t♕\t♔\t♗\t♘\t♖\t" + separator +
            "\ta\tb\tc\td\te\tf\tg\th" + separator;

    @Test
    public void testUnicodeRendering() {
        JChessGame game = JChessGame.newGame();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try(PrintStream stream = new PrintStream(baos, true, StandardCharsets.UTF_8)) {
            UnicodePositionRenderer.render(stream, game.getPosition(), "*", " ");
        }

        String str = baos.toString(StandardCharsets.UTF_8);
        assertEquals(expectedOutput, str);
    }
}
