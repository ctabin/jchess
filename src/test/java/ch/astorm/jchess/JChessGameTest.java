
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class JChessGameTest {
    @Test
    public void testInitialPosition() {
        JChessGame game = JChessGame.newGame();
        assertEquals(20, game.getAvailableMoves().size());

        Coordinate kingLocation = game.getPosition().findLocation(King.class, Color.WHITE);
        Moveable king = game.getPosition().get(kingLocation);
        assertEquals(0, game.getAvailableMoves(king).size());

        String str = PositionRenderer.render(game.getPosition());
        assertEquals(648, str.length());
    }

    @Test
    public void testDrawByInsufficiantMaterialKingKing() {
        JChessGame game = JChessGame.newEmptyGame(Color.WHITE);
        game.getPosition().put(0, 0, new King(Color.WHITE));
        game.getPosition().put(7, 7, new King(Color.BLACK));

        MoveParser parser = new MoveParser(game);
        assertEquals(JChessGame.Status.DRAW, parser.doMove("Ka2"));
    }

    @Test
    public void testDrawByInsufficiantMaterialKingBishop() {
        {
            JChessGame game = JChessGame.newEmptyGame(Color.WHITE);
            game.getPosition().put(0, 0, new King(Color.WHITE));
            game.getPosition().put(7, 7, new King(Color.BLACK));
            game.getPosition().put(6, 6, new Bishop(Color.BLACK));

            MoveParser parser = new MoveParser(game);
            assertEquals(JChessGame.Status.DRAW, parser.doMove("Ka2"));
        }

        {
            JChessGame game = JChessGame.newEmptyGame(Color.WHITE);
            game.getPosition().put(0, 0, new King(Color.BLACK));
            game.getPosition().put(7, 7, new King(Color.WHITE));
            game.getPosition().put(6, 6, new Bishop(Color.WHITE));

            MoveParser parser = new MoveParser(game);
            assertEquals(JChessGame.Status.DRAW, parser.doMove("Kg8"));
        }
    }

    @Test
    public void testDrawByInsufficiantMaterialKingKnight() {
        {
            JChessGame game = JChessGame.newEmptyGame(Color.WHITE);
            game.getPosition().put(0, 0, new King(Color.WHITE));
            game.getPosition().put(7, 7, new King(Color.BLACK));
            game.getPosition().put(6, 6, new Knight(Color.BLACK));

            MoveParser parser = new MoveParser(game);
            assertEquals(JChessGame.Status.DRAW, parser.doMove("Ka2"));
        }

        {
            JChessGame game = JChessGame.newEmptyGame(Color.WHITE);
            game.getPosition().put(0, 0, new King(Color.BLACK));
            game.getPosition().put(7, 7, new King(Color.WHITE));
            game.getPosition().put(6, 6, new Knight(Color.WHITE));

            MoveParser parser = new MoveParser(game);
            assertEquals(JChessGame.Status.DRAW, parser.doMove("Kg8"));
        }
    }

    @Test
    public void testNoDrawSufficiantMaterial() {
        {
            JChessGame game = JChessGame.newEmptyGame(Color.WHITE);
            game.getPosition().put(0, 0, new King(Color.WHITE));
            game.getPosition().put(7, 7, new King(Color.BLACK));
            game.getPosition().put(6, 6, new Rook(Color.BLACK));

            MoveParser parser = new MoveParser(game);
            assertEquals(JChessGame.Status.NOT_FINISHED, parser.doMove("Ka2"));
        }

        {
            JChessGame game = JChessGame.newEmptyGame(Color.WHITE);
            game.getPosition().put(0, 0, new King(Color.BLACK));
            game.getPosition().put(7, 7, new King(Color.WHITE));
            game.getPosition().put(6, 6, new Rook(Color.WHITE));

            MoveParser parser = new MoveParser(game);
            assertEquals(JChessGame.Status.NOT_FINISHED, parser.doMove("Kg8"));
        }
    }

    @Test
    public void testDrawByNoCapture() {
        JChessGame game = JChessGame.newEmptyGame(Color.WHITE);
        game.getPosition().put(0, 0, new King(Color.WHITE));
        game.getPosition().put(7, 7, new King(Color.BLACK));
        game.getPosition().put(6, 6, new Rook(Color.BLACK));

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
        game.getPosition().put(0, 0, new King(Color.WHITE));
        game.getPosition().put(7, 7, new King(Color.BLACK));
        game.getPosition().put(6, 6, new Rook(Color.BLACK));

        MoveParser parser = new MoveParser(game);
        while(game.getStatus()==Status.NOT_FINISHED) {
            parser.doMove("Ka2");
            parser.doMove("Kg8");
            parser.doMove("Ka1");
            parser.doMove("Kh8");
        }

        assertEquals(Status.DRAW_REPETITION, game.getStatus());
    }

    @Test
    public void testDrawByStalemate() {
        JChessGame game = JChessGame.newEmptyGame(Color.WHITE);
        game.getPosition().put(0, 0, new King(Color.WHITE));
        game.getPosition().put(0, 1, new Queen(Color.WHITE));
        game.getPosition().put(7, 7, new King(Color.BLACK));

        MoveParser parser = new MoveParser(game);
        assertEquals(Status.DRAW_STALEMATE, parser.doMove("Qg6"));
        assertThrows(IllegalStateException.class, () -> parser.doMove("Qg5"));
    }
}
