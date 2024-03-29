
package ch.astorm.jchess.io;

import ch.astorm.jchess.JChessGame;
import ch.astorm.jchess.JChessGame.Status;
import ch.astorm.jchess.core.entities.Pawn;
import ch.astorm.jchess.io.PGNReader.PGNReaderException;
import java.io.StringReader;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;


public class PGNReaderTest {
    @Test
    public void testValidPGN() throws Exception {
        String pgn =    "[Event \"Wch U20\"]\n" +
                        "\n" +
                        "1.e4 d6 2.d4  0-1";
        try(PGNReader parser = new PGNReader(new StringReader(pgn))) {
            JChessGame game = parser.readGame();
            assertEquals(Status.WIN_BLACK, game.getStatus());
        }
    }

    @Test
    public void testValidPGNNoHeader() throws Exception {
        String pgn =    "1.e4 d6 2.d4  0-1";
        try(PGNReader parser = new PGNReader(new StringReader(pgn))) {
            JChessGame game = parser.readGame();
            assertEquals(Status.WIN_BLACK, game.getStatus());
        }
    }

    @Test
    public void testMuptileValidPGN() throws Exception {
        String pgn =    "[Event \"Wch U20\"]\n" +
                        "\n" +
                        "1.e4 d6 2.d4  0-1\n\n" +
                        "[Event \"Wch U20\"]\n" +
                        "\n" +
                        "1.e4 d6 2.d4  1-0\n\n" +
                        "[Event \"Wch U20\"]\n" +
                        "\n" +
                        "1.e4 d6 2.d4  1/2-1/2";
        try(PGNReader parser = new PGNReader(new StringReader(pgn))) {
            JChessGame game1 = parser.readGame();
            assertEquals(Status.WIN_BLACK, game1.getStatus());
            JChessGame game2 = parser.readGame();
            assertEquals(Status.WIN_WHITE, game2.getStatus());
            JChessGame game3 = parser.readGame();
            assertEquals(Status.DRAW, game3.getStatus());
            JChessGame game4 = parser.readGame();
            assertNull(game4);
        }
    }

    @Test
    public void testMuptileValidPGNNoHeader() throws Exception {
        String pgn =    "1.e4 d6 2.d4  0-1\n\n" +
                        "1.e4 d6 2.d4  1-0\n\n" +
                        "1.e4 d6 2.d4  1/2-1/2";
        try(PGNReader parser = new PGNReader(new StringReader(pgn))) {
            JChessGame game1 = parser.readGame();
            assertEquals(Status.WIN_BLACK, game1.getStatus());
            JChessGame game2 = parser.readGame();
            assertEquals(Status.WIN_WHITE, game2.getStatus());
            JChessGame game3 = parser.readGame();
            assertEquals(Status.DRAW, game3.getStatus());
            JChessGame game4 = parser.readGame();
            assertNull(game4);
        }
    }

    @Test
    public void testInvalidPGN() throws Exception {
        String pgn =    "[Event \"Wch U20\"]\n" +
                        "[Site \"Kiljava\"]\n" +
                        "[Date \"1984.??.??\"]\n" +
                        "[Round \"?\"]\n" +
                        "[White \"Anand, Viswanathan\"]\n" +
                        "[Black \"Wolff, Patrick G\"]\n" +
                        "[Result \"0-1\"]\n" +
                        "[WhiteElo \"2285\"]\n" +
                        "[BlackElo \"2225\"]\n" +
                        "[ECO \"B09\"]\n" +
                        "\n" +
                        "1.e4 d6 2.d5  0-1";
        try(PGNReader parser = new PGNReader(new StringReader(pgn))) {
            JChessGame game = parser.readGame();
            fail("Parsing should have failed");
        } catch(PGNReaderException pe) {
            assertNotNull(pe.getGame());
            assertEquals(3, pe.getMoves().size());
            assertEquals("d5", pe.getFailedMove());
        }
    }

    @Test
    public void testInvalidPGN2() throws Exception {
        String pgn =    "[Event \"Wch U20\"]\n" +
                        "[Site \"Kiljava\"]\n" +
                        "[Date \"1984.??.??\"]\n" +
                        "[Round \"?\"]\n" +
                        "[White \"Anand, Viswanathan\"]\n" +
                        "[Black \"Wolff, Patrick G\"]\n" +
                        "[Result \"0-1\"]\n" +
                        "[WhiteElo \"2285\"]\n" +
                        "[BlackElo \"2225\"]\n" +
                        "[ECO \"B09\"]\n" +
                        "\n" +
                        "0.e4 d6 1.d5  0-1";
        try(PGNReader parser = new PGNReader(new StringReader(pgn))) {
            JChessGame game = parser.readGame();
            fail("Parsing should have failed");
        } catch(IllegalArgumentException pe) {
            /* ok */
        }
    }

    @Test
    public void testInvalidResultGame() throws Exception {
        String pgn =    "[Event \"Wch U20\"]\n" +
                        "[Site \"Kiljava\"]\n" +
                        "[Date \"1984.??.??\"]\n" +
                        "[Round \"?\"]\n" +
                        "[White \"Anand, Viswanathan\"]\n" +
                        "[Black \"Wolff, Patrick G\"]\n" +
                        "[Result \"0-1\"]\n" +
                        "[WhiteElo \"2285\"]\n" +
                        "[BlackElo \"2225\"]\n" +
                        "[ECO \"B09\"]\n" +
                        "\n" +
                        "1.e4 d6 2.d4  1-1";
        try(PGNReader parser = new PGNReader(new StringReader(pgn))) {
            JChessGame game = parser.readGame();
            fail("Parsing should have failed");
        } catch(IllegalStateException pe) {
            /* ok */
        }
    }

    @Test
    public void testUnfinishedGame() throws Exception {
        String pgn =    "[Event \"Wch U20\"]\n" +
                        "[Site \"Kiljava\"]\n" +
                        "[Date \"1984.??.??\"]\n" +
                        "[Round \"?\"]\n" +
                        "[White \"Anand, Viswanathan\"]\n" +
                        "[Black \"Wolff, Patrick G\"]\n" +
                        "[Result \"0-1\"]\n" +
                        "[WhiteElo \"2285\"]\n" +
                        "[BlackElo \"2225\"]\n" +
                        "[ECO \"B09\"]\n" +
                        "\n" +
                        "1.e4 d6 2.d4 d5";
        try(PGNReader parser = new PGNReader(new StringReader(pgn))) {
            JChessGame game = parser.readGame();
            fail("Parsing should have failed");
        } catch(IllegalStateException pe) {
            /* ok */
        }
    }

    @Test
    public void testGameNoMovesWithMetadata() throws Exception {
        checkGameNoMovesWithMetadata("1-0", Status.WIN_WHITE);
        checkGameNoMovesWithMetadata("1/2-1/2", Status.DRAW);
        checkGameNoMovesWithMetadata("*", Status.NOT_FINISHED);
    }

    private void checkGameNoMovesWithMetadata(String result, Status status) throws Exception{
        String pgn =    "[Event \"Wch U20\"]\n" +
                "[Site \"Kiljava\"]\n" +
                "[Date \"1984.??.??\"]\n" +
                "[Round \"?\"]\n" +
                "[White \"Anand, Viswanathan\"]\n" +
                "[Black \"Wolff, Patrick G\"]\n" +
                "[Result \"" + result + "\"]\n" +
                "[WhiteElo \"2285\"]\n" +
                "[BlackElo \"2225\"]\n" +
                "[ECO \"B09\"]\n" +
                "\n" +
                "" + result; // no moves, only result
        PGNReader parser = new PGNReader(new StringReader(pgn));
        JChessGame game = parser.readGame();
        assertNotNull(game);
        // check some metadata
        assertEquals(game.getMetadata().get("White"), "Anand, Viswanathan");
        assertEquals(game.getMetadata().get("Black"), "Wolff, Patrick G");
        assertEquals(game.getMetadata().get("Result"), result);
        assertEquals(game.getStatus(), status);
    }
}
