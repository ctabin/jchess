
package ch.astorm.jchess;

import ch.astorm.jchess.JChessGame.Status;
import ch.astorm.jchess.core.Color;
import ch.astorm.jchess.core.Coordinate;
import ch.astorm.jchess.core.Move;
import ch.astorm.jchess.core.Moveable;
import ch.astorm.jchess.core.entities.Bishop;
import ch.astorm.jchess.core.entities.King;
import ch.astorm.jchess.core.entities.Knight;
import ch.astorm.jchess.core.entities.Queen;
import ch.astorm.jchess.core.entities.Rook;
import ch.astorm.jchess.core.rules.RuleManager;
import ch.astorm.jchess.io.MoveParser;
import ch.astorm.jchess.util.PositionRenderer;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class JChessGameTest {
    @Test
    public void testInitialPosition() {
        JChessGame game = JChessGame.newGame();
        assertNull(game.get("e5"));
        assertNotNull(game.get("a1"));

        assertEquals(20, game.getAvailableMoves().size());

        Coordinate kingLocation = game.getPosition().findLocation(King.class, Color.WHITE);
        Moveable king = game.getPosition().get(kingLocation);
        assertEquals(0, game.getAvailableMoves(king).size());

        String str = PositionRenderer.render(game.getPosition());
        assertEquals(648, str.length());

        assertNull(game.getAvailableMoves("e5"));
        assertTrue(game.getAvailableMoves("e1").isEmpty());

        game.doMove(game.getAvailableMoves("e2").get(0));
        game.resign(Color.WHITE);

        assertThrows(IllegalStateException.class, () -> game.doMove(null));
    }

    @Test
    public void testDrawByInsufficiantMaterialKingKing() {
        JChessGame game = JChessGame.newEmptyGame(Color.WHITE);
        game.put("a1", new King(Color.WHITE));
        game.put("h8", new King(Color.BLACK));
        assertFalse(game.getStatus().isFinished());

        assertEquals(false, game.back());

        assertEquals(JChessGame.Status.DRAW, game.doMove("Ka2"));
        assertTrue(game.getStatus().isFinished());
    }

    @Test
    public void testDrawByInsufficiantMaterialKingBishop() {
        {
            JChessGame game = JChessGame.newEmptyGame(Color.WHITE);
            game.put("a1", new King(Color.WHITE));
            game.put("h8", new King(Color.BLACK));
            game.put("g7", new Bishop(Color.BLACK));
            assertEquals(JChessGame.Status.DRAW, game.doMove("Ka2"));
        }

        {
            JChessGame game = JChessGame.newEmptyGame(Color.WHITE);
            game.put("a1", new King(Color.BLACK));
            game.put("h8", new King(Color.WHITE));
            game.put("g7", new Bishop(Color.WHITE));
            assertEquals(JChessGame.Status.DRAW, game.doMove("Kg8"));
        }
    }

    @Test
    public void testDrawByInsufficiantMaterialKingKnight() {
        {
            JChessGame game = JChessGame.newEmptyGame(Color.WHITE);
            game.put("a1", new King(Color.WHITE));
            game.put("h8", new King(Color.BLACK));
            game.put("g7", new Knight(Color.BLACK));
            assertEquals(JChessGame.Status.DRAW, game.doMove("Ka2"));
        }

        {
            JChessGame game = JChessGame.newEmptyGame(Color.WHITE);
            game.put("a1", new King(Color.BLACK));
            game.put("h8", new King(Color.WHITE));
            game.put("g7", new Knight(Color.WHITE));
            assertEquals(JChessGame.Status.DRAW, game.doMove("Kg8"));
        }
    }

    @Test
    public void testNoDrawSufficiantMaterial() {
        {
            JChessGame game = JChessGame.newEmptyGame(Color.WHITE);
            game.put("a1", new King(Color.WHITE));
            game.put("h8", new King(Color.BLACK));
            game.put("g7", new Rook(Color.BLACK));
            assertEquals(JChessGame.Status.NOT_FINISHED, game.doMove("Ka2"));
        }

        {
            JChessGame game = JChessGame.newEmptyGame(Color.WHITE);
            game.put("a1", new King(Color.BLACK));
            game.put("h8", new King(Color.WHITE));
            game.put("g7", new Rook(Color.WHITE));
            assertEquals(JChessGame.Status.NOT_FINISHED, game.doMove("Kg8"));
        }
    }

    @Test
    public void testDrawByNoCapture() {
        JChessGame game = JChessGame.newEmptyGame(Color.WHITE);
        game.put("a1", new King(Color.WHITE));
        game.put("h8", new King(Color.BLACK));
        game.put("g7", new Rook(Color.BLACK));

        int incr = 0;
        while(game.getStatus()==Status.NOT_FINISHED) {
            List<Move> available = game.getAvailableMoves();
            
            Move move = available.get(incr % available.size());
            while(move.getCapturedEntity()!=null) {
                ++incr;
                move = available.get(incr % available.size());
            }

            game.apply(move);
            ++incr;
        }

        assertEquals(Status.DRAW_NOCAPTURE, game.getStatus());
        assertEquals(RuleManager.FORCED_DRAW_MOVE_LIMIT*2, game.getPosition().getMoveHistory().size());
    }

    @Test
    public void testDrawByRepetition() {
        JChessGame game = JChessGame.newEmptyGame(Color.WHITE);
        game.put("a1", new King(Color.WHITE));
        game.put("h8", new King(Color.BLACK));
        game.put("g7", new Rook(Color.BLACK));

        while(game.getStatus()==Status.NOT_FINISHED) {
            game.doMove("Ka2");
            game.doMove("Kg8");
            game.doMove("Ka1");
            game.doMove("Kh8");
        }

        assertEquals(Status.DRAW_REPETITION, game.getStatus());
    }

    @Test
    public void testDrawByStalemate() {
        JChessGame game = JChessGame.newEmptyGame(Color.WHITE);
        game.put("a1", new King(Color.WHITE));
        game.put("b1", new Queen(Color.WHITE));
        game.put("h8", new King(Color.BLACK));

        assertEquals(Status.DRAW_STALEMATE, game.doMove("Qg6"));
        assertThrows(IllegalStateException.class, () -> game.doMove("Qg5"));
    }
}
