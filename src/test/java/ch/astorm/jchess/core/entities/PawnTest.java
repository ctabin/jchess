
package ch.astorm.jchess.core.entities;

import ch.astorm.jchess.JChessGame;
import ch.astorm.jchess.io.MoveParser.InvalidMoveException;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class PawnTest {
    @Test
    public void testEnPassantWhite() {
        JChessGame game = JChessGame.newGame();
        game.play("e4", "h6");
        game.play("e5", "h5");
        game.play("c4", "h4");
        game.play("c5", "d5");

        game.play("cxd6"); assertNotNull(game.back());
        game.play("exd6"); assertNotNull(game.back());

        assertNotNull(game.back());
        game.play("d6");
        game.play("h3");
        game.play("d5");

        assertThrows(InvalidMoveException.class, () -> game.play("exd6"));
        assertThrows(InvalidMoveException.class, () -> game.play("cxd6"));
    }

    @Test
    public void testEnPassantBlack() {
        JChessGame game = JChessGame.newGame();
        game.play("h3", "e5");
        game.play("h4", "c5");
        game.play("h5", "e4");
        game.play("h6", "c4");
        game.play("d4");

        game.play("cxd3"); assertNotNull(game.back());
        game.play("exd3"); assertNotNull(game.back());
        assertNotNull(game.back());

        game.play("d3");
        game.play("a6");
        game.play("d4");

        assertThrows(InvalidMoveException.class, () -> game.play("cxd3"));
        assertThrows(InvalidMoveException.class, () -> game.play("exd3"));
    }

    @Test
    public void testPromotions() {
        JChessGame game = JChessGame.newGame();
        game.play("h4", "a5");
        game.play("h5", "a4");
        game.play("h6", "a3");
        game.play("hxg7", "axb2");

        assertThrows(InvalidMoveException.class, () -> game.play("gxh8=K"));
        assertThrows(InvalidMoveException.class, () -> game.play("gxh8=x"));
        assertThrows(InvalidMoveException.class, () -> game.play("Nc3=Q"));

        game.play("gxh8=R", "bxa1=R");
        game.play("Rxg8", "Rxb1");
        game.play("Rh8", "Ra1");
    }

    @Test
    public void testInvalidPush() {
        JChessGame game = JChessGame.newGame();
        game.play("Nc3", "Nc6");

        assertThrows(InvalidMoveException.class, () -> game.play("c3"));
        assertThrows(InvalidMoveException.class, () -> game.play("c4"));

        game.play("a3");

        assertThrows(InvalidMoveException.class, () -> game.play("c6"));
        assertThrows(InvalidMoveException.class, () -> game.play("c5"));
    }
}
