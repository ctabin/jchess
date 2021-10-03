
package ch.astorm.jchess.io;

import ch.astorm.jchess.JChessGame;
import ch.astorm.jchess.JChessGame.Status;
import ch.astorm.jchess.core.Color;
import ch.astorm.jchess.core.Coordinate;
import ch.astorm.jchess.core.Move;
import ch.astorm.jchess.core.Moveable;
import ch.astorm.jchess.core.entities.Bishop;
import ch.astorm.jchess.core.entities.King;
import ch.astorm.jchess.core.entities.Knight;
import ch.astorm.jchess.core.entities.Pawn;
import ch.astorm.jchess.core.entities.Queen;
import ch.astorm.jchess.core.entities.Rook;
import ch.astorm.jchess.util.PositionRenderer;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Handles the parsing of a move (generally from a PGN file).
 */
public class MoveParser {
    private JChessGame game;

    private static final char CAPTURE_SEPARATOR = 'x';
    private static final char PROMOTION_SEPARATOR = '=';
    private static final char CHECK = '+';
    private static final String SMALL_CASTLE = "O-O";
    private static final String BIG_CASTLE = "O-O-O";

    private static final Map<Character, Integer> COLUMN_MAPPING = new HashMap<>(){{
        put('a', 0);
        put('b', 1);
        put('c', 2);
        put('d', 3);
        put('e', 4);
        put('f', 5);
        put('g', 6);
        put('h', 7);
    }};

    private static final Map<Character, Class<? extends Moveable>> ENTITY_MAPPING = new HashMap<>(){{
        put('K', King.class);
        put('Q', Queen.class);
        put('B', Bishop.class);
        put('N', Knight.class);
        put('R', Rook.class);
    }};

    public MoveParser(JChessGame game) {
        this.game = game;
    }

    /**
     * Exception thrown when an invalid move is given as input.
     */
    public static class InvalidMoveException extends RuntimeException {
        private String moveStr;

        public InvalidMoveException(String message, String moveStr) {
            super(message);
            this.moveStr = moveStr;
        }

        /**
         * Returns the invalid move string (eg 'e6').
         */
        public String getMoveString() {
            return moveStr;
        }
    }

    /**
     * Do all the specified {@code moves} in the {@link JChessGame}.
     *
     * @param moves The moves to apply.
     * @return The status after the last move.
     */
    public Status doMoves(Collection<String> moves) {
        moves.forEach(m -> doMove(m));
        return game.getStatus();
    }

    /**
     * Do the move in the underlying {@link JChessGame}.
     *
     * @param moveStr The algebraic chess notation move.
     * @return The new game status.
     */
    public Status doMove(String moveStr) {
        Move move = getMove(moveStr);
        return game.apply(move);
    }

    /**
     * Returns the corresponding {@code str} move in the {@link JChessGame}.
     *
     * @param moveStr The algebraic chess notation move (such as 'e4' or 'Kb3').
     * @return The move or null if invalid.
     */
    public Move getMove(String moveStr) {
        if(!game.getStatus().isPlayAllowed()) { throw new IllegalStateException("Game is "+game.getStatus()); }

        Move move = getMoveWithoutPromotion(moveStr);
        int equals = moveStr.indexOf(PROMOTION_SEPARATOR);
        if(equals<0) { return move; }
        if(move.getDisplacement().getMoveable().getClass()!=Pawn.class) { throw new InvalidMoveException(moveStr+" is not a pawn move", moveStr); }

        char promChar = moveStr.charAt(equals+1);
        Class<? extends Moveable> pieceClass = ENTITY_MAPPING.getOrDefault(promChar, Pawn.class);
        if(pieceClass==Pawn.class) { throw new InvalidMoveException(moveStr+" promotes to a pawn", moveStr); }
        if(pieceClass==King.class) { throw new InvalidMoveException(moveStr+" promotes to a King", moveStr); }

        try {
            Moveable promotion = pieceClass.getConstructor(Color.class).newInstance(move.getDisplacement().getMoveable().getColor());
            move.setPromotion(promotion);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        return move;
    }

    private Move getMoveWithoutPromotion(String moveStr) {
        Color colorToMove = game.getColorToMove();
        List<Move> availableMoves = game.getAvailableMoves();
        if(moveStr.startsWith(SMALL_CASTLE) || moveStr.startsWith(BIG_CASTLE)) {
            String cleanedMove = moveStr.replace(""+CHECK, "");
            Optional<Move> castleMove = availableMoves.stream().
                    filter(m -> m.getDisplacement().getMoveable().getColor()==colorToMove).
                    filter(m -> m.getDisplacement().getMoveable().getClass()==King.class).
                    filter(m -> m.getLinkedDisplacements()!=null && m.getLinkedDisplacements().size()==1).
                    filter(m -> cleanedMove.equals(SMALL_CASTLE) ? m.getDisplacement().getNewLocation().getColumn()==6 : m.getDisplacement().getNewLocation().getColumn()==2).
                    findFirst();
            return castleMove.orElseThrow(() -> new InvalidMoveException(moveStr+" is not a legal move", moveStr));
        }

        int charPosition = 0;
        char pieceLetter = moveStr.charAt(charPosition);
        Class<? extends Moveable> pieceClass = ENTITY_MAPPING.getOrDefault(pieceLetter, Pawn.class);
        if(pieceClass!=Pawn.class) { ++charPosition; }

        boolean isCapture = false;
        char ambigousMoveCharFilter = 0;
        char columnChar = moveStr.charAt(charPosition);
        char nextChar = moveStr.charAt(charPosition+1);
        if((columnChar>='1' && columnChar<='8') || (nextChar>='a' && nextChar<='h')) {
            ++charPosition;
            isCapture = columnChar==CAPTURE_SEPARATOR;
            ambigousMoveCharFilter = columnChar;
            columnChar = nextChar;
        } else if(nextChar==CAPTURE_SEPARATOR) { //cxb5
            charPosition += 2;
            ambigousMoveCharFilter = columnChar;
            columnChar = moveStr.charAt(charPosition);
            isCapture = true;
        }

        Integer columnIndex = COLUMN_MAPPING.get(columnChar);
        if(columnIndex==null) {
            if(columnChar!=CAPTURE_SEPARATOR) { throw new IllegalArgumentException("Invalid move: "+moveStr); }
            isCapture = true;

            ++charPosition;
            columnChar = moveStr.charAt(charPosition);
            columnIndex = COLUMN_MAPPING.get(columnChar);
            if(columnIndex==null) { throw new IllegalArgumentException("Invalid move: "+moveStr); }
        }

        ++charPosition;
        char rowChar = moveStr.charAt(charPosition);
        int rowIndex;
        try { rowIndex = Integer.parseInt(rowChar+"")-1; }
        catch(NumberFormatException nfe) { throw new IllegalArgumentException("Invalid move (parsing "+rowChar+"): "+moveStr); }

        Coordinate target = new Coordinate(rowIndex, columnIndex);
        if(!game.getPosition().getBoard().isValid(target)) { throw new IllegalArgumentException("Invalid move: "+moveStr); }

        boolean filterCapture = isCapture;
        List<Move> possibleMoves = availableMoves.stream().
                filter(m -> m.getDisplacement().getMoveable().getClass()==pieceClass).
                filter(m -> m.getDisplacement().getNewLocation().equals(target)).
                filter(m -> filterCapture ? m.getCapturedEntity()!=null : /*m.getCapturedEntity()==null*/true). /* actually, some times the PGN doesn't explicitely capture */
                collect(Collectors.toList());
        if(possibleMoves.isEmpty()) { throw new InvalidMoveException(moveStr+" is not a legal move ("+columnIndex+","+rowIndex+", capture="+filterCapture+", "+pieceClass.getSimpleName()+")", moveStr); }
        if(possibleMoves.size()==1) { return possibleMoves.get(0); }
        if(possibleMoves.size()>1 && ambigousMoveCharFilter==0) { throw new InvalidMoveException(moveStr+" is ambiguous ("+possibleMoves.size()+" possible moves for "+colorToMove+")", moveStr); }

        int ambigousRowIndexFilter;
        int ambigousColumnIndexFilter;
        if(ambigousMoveCharFilter>='a' && ambigousMoveCharFilter<='h') {
            ambigousRowIndexFilter = -1;
            ambigousColumnIndexFilter = COLUMN_MAPPING.get(ambigousMoveCharFilter);
        } else {
            ambigousRowIndexFilter = Integer.parseInt(""+ambigousMoveCharFilter)-1;
            ambigousColumnIndexFilter = -1;
        }

        List<Move> filteredMoves = possibleMoves.stream().
                filter(m -> ambigousColumnIndexFilter==-1 || m.getDisplacement().getOldLocation().getColumn()==ambigousColumnIndexFilter).
                filter(m -> ambigousRowIndexFilter==-1 || m.getDisplacement().getOldLocation().getRow()==ambigousRowIndexFilter).
                collect(Collectors.toList());
        if(filteredMoves.isEmpty()) { throw new InvalidMoveException(moveStr+" does not exist (filtered out of "+possibleMoves.size()+" possible moves)", moveStr); }
        if(filteredMoves.size()>1) { throw new InvalidMoveException(moveStr+" is [still] ambiguous ("+possibleMoves.size()+" possible moves for "+colorToMove+")", moveStr); }
        return filteredMoves.get(0);
    }
}
