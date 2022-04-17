
package ch.astorm.jchess.io;

import ch.astorm.jchess.JChessGame;
import ch.astorm.jchess.JChessGame.Status;
import ch.astorm.jchess.core.Color;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

public class PGNWriterTest {
    @Test
    public void testSimpleGame() {
        JChessGame game = JChessGame.newGame();

        game.play("e4","e5","Nf3","Nc6","Bb5");
        game.draw();

        StringWriter sw = new StringWriter();
        try(PGNWriter writer = new PGNWriter(sw)) { writer.writeGame(game); }

        assertEquals("[Event \"\"]\n" +
                     "[Site \"\"]\n" +
                     "[Date \"????.??.??\"]\n" +
                     "[Round \"\"]\n" +
                     "[White \"\"]\n" +
                     "[Black \"\"]\n" +
                     "[Result \"1/2-1/2\"]\n" +
                     "\n" +
                     "1.e4 e5 2.Nf3 Nc6 3.Bb5  1/2-1/2\n\n", sw.toString());
    }

    @Test
    public void testUnfinishedGame() throws Exception {
        JChessGame game = JChessGame.newGame();

        game.play("e4","e5");

        StringWriter sw = new StringWriter();
        try(PGNWriter writer = new PGNWriter(sw)) { writer.writeGame(game); }

        assertEquals("[Event \"\"]\n" +
                     "[Site \"\"]\n" +
                     "[Date \"????.??.??\"]\n" +
                     "[Round \"\"]\n" +
                     "[White \"\"]\n" +
                     "[Black \"\"]\n" +
                     "[Result \"*\"]\n" +
                     "\n" +
                     "1.e4 e5 *\n\n", sw.toString());

        try(PGNReader reader = new PGNReader(new StringReader(sw.toString()))) { game = reader.readGame(); }
        assertEquals(Status.NOT_FINISHED, game.getStatus());
    }

    @Test
    public void testMultipleGames() throws IOException {
        JChessGame game1 = JChessGame.newGame();
        {
            game1.play("e4","e5","Nf3","Nc6","Bb5");
            game1.draw();
        }

        JChessGame game2 = JChessGame.newGame();
        {
            game2.play("e4","e5","Nf3","Nc6");
            game2.resign(Color.WHITE);
        }

        JChessGame game3 = JChessGame.newGame();
        {
            game3.play("e4","e5","Nf3","Nc6");
            game3.resign(Color.BLACK);
        }

        StringWriter sw = new StringWriter();
        try(PGNWriter writer = new PGNWriter(sw)) {
            writer.writeGame(game1);
            writer.writeGame(game2);
            writer.writeGame(game3);
        }

        try(PGNReader reader = new PGNReader(new StringReader(sw.toString()))) {
            JChessGame readGame1 = reader.readGame();
            assertEquals(Status.DRAW, readGame1.getStatus());

            JChessGame readGame2 = reader.readGame();
            assertEquals(Status.WIN_BLACK, readGame2.getStatus());

            JChessGame readGame3 = reader.readGame();
            assertEquals(Status.WIN_WHITE, readGame3.getStatus());

            assertNull(reader.readGame());
        }
    }
}
