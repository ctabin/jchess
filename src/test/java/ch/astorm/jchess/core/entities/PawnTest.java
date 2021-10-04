
package ch.astorm.jchess.core.entities;

import ch.astorm.jchess.JChessGame;
import ch.astorm.jchess.io.MoveParser;
import ch.astorm.jchess.io.MoveParser.InvalidMoveException;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class PawnTest {
    @Test
    public void testEnPassantWhite() {
        JChessGame game = JChessGame.newGame();
        MoveParser mover = new MoveParser(game);
        mover.doMoves(Arrays.asList("e4", "h6"));
        mover.doMoves(Arrays.asList("e5", "h5"));
        mover.doMoves(Arrays.asList("c4", "h4"));
        mover.doMoves(Arrays.asList("c5", "d5"));

        mover.doMove("cxd6"); assertTrue(game.back());
        mover.doMove("exd6"); assertTrue(game.back());

        assertTrue(game.back());
        mover.doMove("d6");
        mover.doMove("h3");
        mover.doMove("d5");

        assertThrows(InvalidMoveException.class, () -> mover.doMove("exd6"));
        assertThrows(InvalidMoveException.class, () -> mover.doMove("cxd6"));
    }

    @Test
    public void testEnPassantBlack() {
        JChessGame game = JChessGame.newGame();
        MoveParser mover = new MoveParser(game);
        mover.doMoves(Arrays.asList("h3", "e5"));
        mover.doMoves(Arrays.asList("h4", "c5"));
        mover.doMoves(Arrays.asList("h5", "e4"));
        mover.doMoves(Arrays.asList("h6", "c4"));
        mover.doMove("d4");

        mover.doMove("cxd3"); assertTrue(game.back());
        mover.doMove("exd3"); assertTrue(game.back());
        assertTrue(game.back());

        mover.doMove("d3");
        mover.doMove("a6");
        mover.doMove("d4");

        assertThrows(InvalidMoveException.class, () -> mover.doMove("cxd3"));
        assertThrows(InvalidMoveException.class, () -> mover.doMove("exd3"));
    }

    @Test
    public void testPromotions() {
        JChessGame game = JChessGame.newGame();
        MoveParser mover = new MoveParser(game);
        mover.doMoves(Arrays.asList("h4", "a5"));
        mover.doMoves(Arrays.asList("h5", "a4"));
        mover.doMoves(Arrays.asList("h6", "a3"));
        mover.doMoves(Arrays.asList("hxg7", "axb2"));

        assertThrows(InvalidMoveException.class, () -> mover.doMove("gxh8=K"));
        assertThrows(InvalidMoveException.class, () -> mover.doMove("gxh8=x"));
        assertThrows(InvalidMoveException.class, () -> mover.doMove("Nc3=Q"));

        mover.doMoves(Arrays.asList("gxh8=R", "bxa1=R"));
        mover.doMoves(Arrays.asList("Rxg8", "Rxb1"));
        mover.doMoves(Arrays.asList("Rh8", "Ra1"));
    }

    @Test
    public void testInvalidPush() {
        JChessGame game = JChessGame.newGame();
        MoveParser mover = new MoveParser(game);
        mover.doMoves(Arrays.asList("Nc3", "Nc6"));

        assertThrows(InvalidMoveException.class, () -> mover.doMove("c3"));
        assertThrows(InvalidMoveException.class, () -> mover.doMove("c4"));

        mover.doMove("a3");

        assertThrows(InvalidMoveException.class, () -> mover.doMove("c6"));
        assertThrows(InvalidMoveException.class, () -> mover.doMove("c5"));
    }
}
