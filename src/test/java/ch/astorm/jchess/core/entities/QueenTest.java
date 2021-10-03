
package ch.astorm.jchess.core.entities;

import ch.astorm.jchess.core.Board;
import ch.astorm.jchess.core.Color;
import ch.astorm.jchess.core.Coordinate;
import ch.astorm.jchess.core.Move;
import ch.astorm.jchess.core.Position;
import ch.astorm.jchess.core.rules.RuleManager;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class QueenTest {
    @Test
    public void testAllDirections() {
        Board board = new Board();
        RuleManager rule = new RuleManager();
        Position position = new Position(board, rule, Color.WHITE);
        
        Queen queen = new Queen(Color.WHITE);
        position.put(4, 4, queen);

        List<Move> moves = rule.getDisplacementRule(queen).getAvailableMoves(position, new Coordinate(4, 4), queen);
        assertEquals(27, moves.size());
    }

    @Test
    public void testEnclosedByPawns() {
        Board board = new Board();
        RuleManager rule = new RuleManager();
        Position position = new Position(board, rule, Color.WHITE);

        Queen queen = new Queen(Color.WHITE);
        position.put(4, 4, queen);
        position.put(3, 3, new Pawn(Color.WHITE));
        position.put(3, 4, new Pawn(Color.WHITE));
        position.put(3, 5, new Pawn(Color.WHITE));
        position.put(4, 3, new Pawn(Color.WHITE));
        position.put(4, 5, new Pawn(Color.WHITE));
        position.put(5, 3, new Pawn(Color.WHITE));
        position.put(5, 4, new Pawn(Color.WHITE));
        position.put(5, 5, new Pawn(Color.WHITE));


        List<Move> moves = rule.getDisplacementRule(queen).getAvailableMoves(position, new Coordinate(4, 4), queen);
        assertEquals(0, moves.size());
    }

    @Test
    public void testSurroundedByPawns() {
        Board board = new Board();
        RuleManager rule = new RuleManager();
        Position position = new Position(board, rule, Color.WHITE);

        Queen queen = new Queen(Color.WHITE);
        position.put(4, 4, queen);
        position.put(3, 3, new Pawn(Color.BLACK));
        position.put(3, 4, new Pawn(Color.BLACK));
        position.put(3, 5, new Pawn(Color.BLACK));
        position.put(4, 3, new Pawn(Color.BLACK));
        position.put(4, 5, new Pawn(Color.BLACK));
        position.put(5, 3, new Pawn(Color.BLACK));
        position.put(5, 4, new Pawn(Color.BLACK));
        position.put(5, 5, new Pawn(Color.BLACK));


        List<Move> moves = rule.getDisplacementRule(queen).getAvailableMoves(position, new Coordinate(4, 4), queen);
        assertEquals(8, moves.size());
    }
}
