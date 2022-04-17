
package ch.astorm.jchess.util;

import ch.astorm.jchess.JChessGame;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class ASCIIPositionRendererTest {
    @Test
    public void testRendering() throws URISyntaxException, IOException  {
        JChessGame game = JChessGame.newGame();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try(PrintStream stream = new PrintStream(baos, true, StandardCharsets.US_ASCII)) {
            ASCIIPositionRenderer.render(stream, game.getPosition());
        }

        String str = baos.toString(StandardCharsets.US_ASCII);
        assertEquals(getExpectedOutput(), str);
    }

    private String getExpectedOutput() throws URISyntaxException, IOException {
        URL expectedOutputTemplateURL = getClass().getResource("ASCIIPositionTemplate.txt");
        Objects.requireNonNull(expectedOutputTemplateURL);
        return Files
                .readString(Path.of(expectedOutputTemplateURL.toURI()))
                .replaceAll("separator", System.lineSeparator());
    }
}
