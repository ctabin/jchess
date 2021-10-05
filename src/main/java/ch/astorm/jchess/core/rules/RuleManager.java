
package ch.astorm.jchess.core.rules;

import ch.astorm.jchess.JChessGame.Status;
import ch.astorm.jchess.core.Color;
import ch.astorm.jchess.core.Coordinate;
import ch.astorm.jchess.core.Move;
import ch.astorm.jchess.core.Moveable;
import ch.astorm.jchess.core.Position;
import ch.astorm.jchess.core.entities.Bishop;
import ch.astorm.jchess.core.entities.King;
import ch.astorm.jchess.core.entities.Knight;
import ch.astorm.jchess.core.entities.Pawn;
import ch.astorm.jchess.core.entities.Queen;
import ch.astorm.jchess.core.entities.Rook;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles the rules of the game.
 */
public class RuleManager {
    private static DisplacementRule<Pawn> RULE_PAWN = new PawnDisplacementRule();
    private static DisplacementRule<King> RULE_KING = new KingDisplacementRule();
    private static DisplacementRule<Moveable> RULE_QUEEN = SimpleDisplacementRule.anyDirections(-1);
    private static DisplacementRule<Moveable> RULE_ROOK = new SimpleDisplacementRule<>().withVerticalHorizontalLookups(-1);
    private static DisplacementRule<Moveable> RULE_BISHOP = new SimpleDisplacementRule<>().withDiagonalLookups(-1);

    private static DisplacementRule<Moveable> RULE_KNIGHT = new SimpleDisplacementRule<>().withCustomLookup(2, 1, 1).
                                                                                           withCustomLookup(2, -1, 1).
                                                                                           withCustomLookup(-2, 1, 1).
                                                                                           withCustomLookup(-2, -1, 1).
                                                                                           withCustomLookup(1, 2, 1).
                                                                                           withCustomLookup(1, -2, 1).
                                                                                           withCustomLookup(-1, 2, 1).
                                                                                           withCustomLookup(-1, -2, 1);

    /**
     * Limit of moves with no capture, nor pawn move where a draw is forced.
     * https://en.wikipedia.org/wiki/Fifty-move_rule#Seventy-five-move_rule
     */
    public static final int FORCED_DRAW_MOVE_LIMIT = 75;

    /**
     * Limit of position repetitions after which a draw is forced.
     * https://en.wikipedia.org/wiki/Threefold_repetition#Fivefold_repetition_rule
     */
    public static final int FORCED_DRAW_POSITION_REPETITION_LIMIT = 5;

    /**
     * Returns the {@link DisplacementRule} corresponding to the specified {@code moveable}.
     *
     * @param moveable The {@link Moveable} entity.
     * @return The displacement rule to apply.
     */
    @SuppressWarnings("unchecked")
    public <T extends Moveable> DisplacementRule<T> getDisplacementRule(T moveable) {
        if(moveable instanceof Pawn) { return (DisplacementRule<T>)RULE_PAWN; }
        if(moveable instanceof Bishop) { return (DisplacementRule<T>)RULE_BISHOP; }
        if(moveable instanceof Knight) { return (DisplacementRule<T>)RULE_KNIGHT; }
        if(moveable instanceof Rook) { return (DisplacementRule<T>)RULE_ROOK; }
        if(moveable instanceof Queen) { return (DisplacementRule<T>)RULE_QUEEN; }
        if(moveable instanceof King) { return (DisplacementRule<T>)RULE_KING; }
        throw new IllegalArgumentException("No displacement rule for "+moveable);
    }

    /**
     * Returns the status according to the current {@code position}.
     *
     * @param position The position.
     * @return The status.
     */
    public Status getEndgameStatus(Position position) {
        Color color = position.getColorToMove();
        List<Move> availableLegalMoves = position.getLegalMoves();
        if(availableLegalMoves.isEmpty()) {
            Coordinate king = position.findLocation(King.class, color);
            if(king==null) { throw new IllegalStateException("No "+color+" king in position"); }

            boolean isInCheck = position.canBeReached(king, color.opposite());
            if(!isInCheck) { return Status.DRAW_STALEMATE; }
            return color==Color.WHITE ? Status.WIN_BLACK : Status.WIN_WHITE;
        }

        /*
        Draw by insufficiant material is somewhat tricky according to wikipedia: https://en.wikipedia.org/wiki/Rules_of_chess#Draws
        Also, some (official) games have a few more moves even in dead drawn positions (king against king).
        */
        if(isDeadPosition(position)) {
            return Status.DRAW;
        }

        //3 times position repetition rule
        Position checkPosition = position;
        while(checkPosition!=null) {
            int nbReached = 0;
            Position previousPosition = checkPosition.getPreviousPosition();
            while(previousPosition!=null) {
                if(checkPosition.equals(previousPosition)) {
                    ++nbReached;
                    if(nbReached>=FORCED_DRAW_POSITION_REPETITION_LIMIT) { return Status.DRAW_REPETITION; }
                }
                
                previousPosition = previousPosition.getPreviousPosition();
            }

            checkPosition = checkPosition.getPreviousPosition();
        }
        
        //75 moves without capture nor pawn push rule
        List<Move> history = position.getMoveHistory();
        if(history.size()>=FORCED_DRAW_MOVE_LIMIT*2) {
            boolean hasCapture = false;
            for(int i=0 ; i<FORCED_DRAW_MOVE_LIMIT*2 ; ++i) {
                Move move = history.get(history.size()-i-1);
                if(move.getCapturedEntity()!=null || move.getDisplacement().getMoveable().getClass()==Pawn.class) {
                    hasCapture = true;
                    break;
                }
            }

            if(!hasCapture) {
                return Status.DRAW_NOCAPTURE;
            }
        }

        return Status.NOT_FINISHED;
    }

    private boolean isDeadPosition(Position position) {
        List<Moveable> whites = position.getMoveables(Color.WHITE).stream().filter(m -> m.getClass()!=King.class).collect(Collectors.toList());
        List<Moveable> blacks = position.getMoveables(Color.BLACK).stream().filter(m -> m.getClass()!=King.class).collect(Collectors.toList());
        if(whites.isEmpty() && blacks.isEmpty()) { return true; } //king against king
        
        if(!whites.isEmpty() && !blacks.isEmpty()) {
            //actually it could be a dead draw depending on the configuration, especially
            //with bishops of the same color.
            //since it is impossible to know for sure, consider it not as a dead draw.
            return false;
        }

        if(whites.isEmpty() && blacks.size()==1) {
            Class<? extends Moveable> blackPiece = blacks.get(0).getClass();
            return blackPiece==Bishop.class || blackPiece==Knight.class; //king against king and bishop/knight
        }

        if(blacks.isEmpty() && whites.size()==1) {
            Class<? extends Moveable> whitePiece = whites.get(0).getClass();
            return whitePiece==Bishop.class || whitePiece==Knight.class; //king against king and bishop/knight
        }

        return false;
    }
}
