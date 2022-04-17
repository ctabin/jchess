
package ch.astorm.jchess.util;

import ch.astorm.jchess.JChessGame;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class ASCIIPositionRendererTest {
    @Test
    public void testRendering() throws URISyntaxException, IOException  {
        JChessGame game = JChessGame.newGame();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try(PrintStream stream = new PrintStream(baos, true, StandardCharsets.US_ASCII)) { ASCIIPositionRenderer.render(stream, game.getPosition()); }
        String str = baos.toString(StandardCharsets.US_ASCII);
        
        String expected;
        try(InputStream is = ASCIIPositionRendererTest.class.getResourceAsStream("ASCIIPositionTemplate.txt")) {
            expected = IOUtils.toString(is, StandardCharsets.US_ASCII).replace("\n", System.lineSeparator());
        }
        
        assertEquals(expected, str);
    }
}
