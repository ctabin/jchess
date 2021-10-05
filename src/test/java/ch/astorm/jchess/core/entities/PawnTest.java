
package ch.astorm.jchess.core.entities;

import ch.astorm.jchess.JChessGame;
import ch.astorm.jchess.io.MoveParser.InvalidMoveException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class PawnTest {
    @Test
    public void testEnPassantWhite() {
        JChessGame game = JChessGame.newGame();
        game.doMove("e4", "h6");
        game.doMove("e5", "h5");
        game.doMove("c4", "h4");
        game.doMove("c5", "d5");

        game.doMove("cxd6"); assertTrue(game.back());
        game.doMove("exd6"); assertTrue(game.back());

        assertTrue(game.back());
        game.doMove("d6");
        game.doMove("h3");
        game.doMove("d5");

        assertThrows(InvalidMoveException.class, () -> game.doMove("exd6"));
        assertThrows(InvalidMoveException.class, () -> game.doMove("cxd6"));
    }

    @Test
    public void testEnPassantBlack() {
        JChessGame game = JChessGame.newGame();
        game.doMove("h3", "e5");
        game.doMove("h4", "c5");
        game.doMove("h5", "e4");
        game.doMove("h6", "c4");
        game.doMove("d4");

        game.doMove("cxd3"); assertTrue(game.back());
        game.doMove("exd3"); assertTrue(game.back());
        assertTrue(game.back());

        game.doMove("d3");
        game.doMove("a6");
        game.doMove("d4");

        assertThrows(InvalidMoveException.class, () -> game.doMove("cxd3"));
        assertThrows(InvalidMoveException.class, () -> game.doMove("exd3"));
    }

    @Test
    public void testPromotions() {
        JChessGame game = JChessGame.newGame();
        game.doMove("h4", "a5");
        game.doMove("h5", "a4");
        game.doMove("h6", "a3");
        game.doMove("hxg7", "axb2");

        assertThrows(InvalidMoveException.class, () -> game.doMove("gxh8=K"));
        assertThrows(InvalidMoveException.class, () -> game.doMove("gxh8=x"));
        assertThrows(InvalidMoveException.class, () -> game.doMove("Nc3=Q"));

        game.doMove("gxh8=R", "bxa1=R");
        game.doMove("Rxg8", "Rxb1");
        game.doMove("Rh8", "Ra1");
    }

    @Test
    public void testInvalidPush() {
        JChessGame game = JChessGame.newGame();
        game.doMove("Nc3", "Nc6");

        assertThrows(InvalidMoveException.class, () -> game.doMove("c3"));
        assertThrows(InvalidMoveException.class, () -> game.doMove("c4"));

        game.doMove("a3");

        assertThrows(InvalidMoveException.class, () -> game.doMove("c6"));
        assertThrows(InvalidMoveException.class, () -> game.doMove("c5"));
    }
}
