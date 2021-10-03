
package ch.astorm.jchess.core.entities;

import ch.astorm.jchess.JChessGame;
import ch.astorm.jchess.core.Board;
import ch.astorm.jchess.core.Color;
import ch.astorm.jchess.core.Coordinate;
import ch.astorm.jchess.core.Move;
import ch.astorm.jchess.core.Position;
import ch.astorm.jchess.core.rules.RuleManager;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class KingTest {
    @Test
    public void testAllDirections() {
        Board board = new Board();
        RuleManager rule = new RuleManager();
        Position position = new Position(board, rule, Color.WHITE);

        King king = new King(Color.WHITE);
        position.put(4, 4, king);

        assertThrows(IllegalArgumentException.class, () -> rule.getDisplacementRule(null));

        List<Move> moves = rule.getDisplacementRule(king).getAvailableMoves(position, new Coordinate(4, 4), king);
        assertEquals(8, moves.size());
    }

    @Test
    public void testEnclosedByPawns() {
        Board board = new Board();
        RuleManager rule = new RuleManager();
        Position position = new Position(board, rule, Color.WHITE);

        King king = new King(Color.WHITE);
        position.put(4, 4, king);
        position.put(3, 3, new Pawn(Color.WHITE));
        position.put(3, 4, new Pawn(Color.WHITE));
        position.put(3, 5, new Pawn(Color.WHITE));
        position.put(4, 3, new Pawn(Color.WHITE));
        position.put(4, 5, new Pawn(Color.WHITE));
        position.put(5, 3, new Pawn(Color.WHITE));
        position.put(5, 4, new Pawn(Color.WHITE));
        position.put(5, 5, new Pawn(Color.WHITE));

        List<Move> moves = rule.getDisplacementRule(king).getAvailableMoves(position, new Coordinate(4, 4), king);
        assertEquals(0, moves.size());
    }

    @Test
    public void testSurroundedByPawns() {
        Board board = new Board();
        RuleManager rule = new RuleManager();
        Position position = new Position(board, rule, Color.WHITE);

        King king = new King(Color.WHITE);
        position.put(4, 4, king);
        position.put(3, 3, new Pawn(Color.BLACK));
        position.put(3, 4, new Pawn(Color.BLACK));
        position.put(3, 5, new Pawn(Color.BLACK));
        position.put(4, 3, new Pawn(Color.BLACK));
        position.put(4, 5, new Pawn(Color.BLACK));
        position.put(5, 3, new Pawn(Color.BLACK));
        position.put(5, 4, new Pawn(Color.BLACK));
        position.put(5, 5, new Pawn(Color.BLACK));

        List<Move> moves = rule.getDisplacementRule(king).getAvailableMoves(position, new Coordinate(4, 4), king);
        assertEquals(5, moves.size());
    }

    @Test
    public void testSmallCastling() {
        JChessGame game = JChessGame.newGame();
        RuleManager rule = game.getRuleManager();

        Position position = game.getPosition();
        King king = (King)position.get(0, 4);

        {
            position.put(0, 5, null);
            List<Move> moves = rule.getDisplacementRule(king).getAvailableMoves(position, position.getLocation(king), king);
            assertEquals(1, moves.size());
        }

        {
            position.put(0, 6, null);
            List<Move> moves = rule.getDisplacementRule(king).getAvailableMoves(position, position.getLocation(king), king);
            assertEquals(2, moves.size());
        }

        {
            position.increaseDisplacementCount(position.get(0, 7), 1);
            List<Move> moves = rule.getDisplacementRule(king).getAvailableMoves(position, position.getLocation(king), king);
            assertEquals(1, moves.size());
        }

        assertEquals(1, game.getAvailableMoves(king).size());
    }

    @Test
    public void testBigCastling() {
        JChessGame game = JChessGame.newGame();
        RuleManager rule = game.getRuleManager();

        Position position = game.getPosition();
        King king = (King)position.get(0, 4);

        {
            position.put(0, 3, null);
            List<Move> moves = rule.getDisplacementRule(king).getAvailableMoves(position, position.getLocation(king), king);
            assertEquals(1, moves.size());
        }

        {
            position.put(0, 2, null);
            List<Move> moves = rule.getDisplacementRule(king).getAvailableMoves(position, position.getLocation(king), king);
            assertEquals(1, moves.size());
        }

        {
            position.put(0, 1, null);
            List<Move> moves = rule.getDisplacementRule(king).getAvailableMoves(position, position.getLocation(king), king);
            assertEquals(2, moves.size());
        }

        {
            position.increaseDisplacementCount(position.get(0, 0), 1);
            List<Move> moves = rule.getDisplacementRule(king).getAvailableMoves(position, position.getLocation(king), king);
            assertEquals(1, moves.size());
        }

        {
            position.increaseDisplacementCount(position.get(0, 0), -1);
            position.put(1, 2, new Bishop(Color.BLACK));
            List<Move> moves = rule.getDisplacementRule(king).getAvailableMoves(position, position.getLocation(king), king);
            assertEquals(0, moves.size());
        }

        {
            position.put(1, 2, null);
            position.put(1, 1, new Bishop(Color.BLACK));
            List<Move> moves = rule.getDisplacementRule(king).getAvailableMoves(position, position.getLocation(king), king);
            assertEquals(1, moves.size());
        }
    }

    @Test
    public void testNoCastling() {
        JChessGame game = JChessGame.newGame();
        RuleManager rule = game.getRuleManager();

        Position position = game.getPosition();
        King king = (King)position.get(0, 4);
        position.put(0, 1, null);
        position.put(0, 2, null);
        position.put(0, 3, null);
        position.put(0, 5, null);
        position.put(0, 6, null);

        {
            List<Move> moves = rule.getDisplacementRule(king).getAvailableMoves(position, position.getLocation(king), king);
            assertEquals(4, moves.size());
        }

        {
            position.increaseDisplacementCount(king, 1);
            List<Move> moves = rule.getDisplacementRule(king).getAvailableMoves(position, position.getLocation(king), king);
            assertEquals(2, moves.size());
        }
    }
}
