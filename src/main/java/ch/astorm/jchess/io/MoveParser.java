
package ch.astorm.jchess.io;

import ch.astorm.jchess.JChessGame;
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
import ch.astorm.jchess.core.rules.Displacement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

/**
 * Handles the parsing of a move (generally from a PGN file).
 */
public class MoveParser {
    private JChessGame game;

    /**
     * Capture separator.
     */
    public static final char CAPTURE_SEPARATOR = 'x';

    /**
     * Promotion separator.
     */
    public static final char PROMOTION_SEPARATOR = '=';

    /**
     * Check sign.
     */
    public static final char CHECK = '+';

    /**
     * Small castling.
     */
    public static final String SMALL_CASTLING = "O-O";

    /**
     * Big castling.
     */
    public static final String BIG_CASTLING = "O-O-O";

    /**
     * Entities mapping.
     */
    public static final BidiMap<Character, Class<? extends Moveable>> ENTITY_MAPPING = new DualHashBidiMap<>(){{
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
     * Returns the corresponding {@code str} move in the {@link JChessGame}.
     *
     * @param moveStr The algebraic chess notation move (such as 'e4' or 'Kb3').
     * @return The move or null if invalid.
     */
    public Move getMove(String moveStr) {
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
        Color colorToMove = game.getColorOnMove();
        List<Move> availableMoves = game.getAvailableMoves();
        if(moveStr.startsWith(SMALL_CASTLING) || moveStr.startsWith(BIG_CASTLING)) {
            String cleanedMove = moveStr.replace(""+CHECK, "");
            Optional<Move> castleMove = availableMoves.stream().
                    filter(m -> m.getDisplacement().getMoveable().getColor()==colorToMove).
                    filter(m -> m.getDisplacement().getMoveable().getClass()==King.class).
                    filter(m -> m.getLinkedDisplacements()!=null && m.getLinkedDisplacements().size()==1).
                    filter(m -> cleanedMove.equals(SMALL_CASTLING) ? m.getDisplacement().getNewLocation().getColumn()==6 : m.getDisplacement().getNewLocation().getColumn()==2).
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

        int maxColumns = game.getPosition().getBoard().getColumnsCount();
        int columnIndex = columnChar-'a';
        if(columnIndex<0 || columnIndex>=maxColumns) {
            if(columnChar!=CAPTURE_SEPARATOR) { throw new IllegalArgumentException("Invalid move: "+moveStr); }
            isCapture = true;

            ++charPosition;
            columnChar = moveStr.charAt(charPosition);
            columnIndex = columnChar-'a';
            if(columnIndex<0 || columnIndex>=maxColumns) { throw new IllegalArgumentException("Invalid move: "+moveStr); }
        }

        ++charPosition;
        char rowChar = moveStr.charAt(charPosition);
        int rowIndex = rowChar-'1';

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
            ambigousColumnIndexFilter = ambigousMoveCharFilter-'a';
        } else {
            ambigousRowIndexFilter = ambigousMoveCharFilter-'1';
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

    /**
     * Returns the {@code move} in algebraic notation.
     */
    public static String getMoveString(Move move) {
        Displacement displacement = move.getDisplacement();
        Moveable moveable = displacement.getMoveable();

        Color currentColor = moveable.getColor();
        Position positionAfter = move.getPositionAfter();
        Coordinate oppositeKingLocation = positionAfter.findLocation(King.class, currentColor.opposite());
        boolean isOppositeKingInCheck = positionAfter.canBeReached(oppositeKingLocation, currentColor);

        if(move.getLinkedDisplacements()!=null) {
            int newColumn = displacement.getNewLocation().getColumn();
            return newColumn==2 ? BIG_CASTLING : SMALL_CASTLING;
        }


        Character moveableChar = ENTITY_MAPPING.getKey(moveable.getClass());
        String moveableStr = moveableChar!=null ? ""+moveableChar : "";

        Coordinate newLocation = displacement.getNewLocation();
        char column = (char)('a'+newLocation.getColumn());
        String newLocationStr = column+""+(newLocation.getRow()+1);

        boolean isCapture = move.getCapturedEntity()!=null;

        String ambiguousStr = "";
        if(moveable instanceof Pawn) {
            if(isCapture) {
                Coordinate oldLocation = displacement.getOldLocation();
                ambiguousStr = ""+((char)('a'+oldLocation.getColumn()));
            }
        } else {
            boolean isAmbigous = false;
            boolean ambigousColumn = false;
            boolean ambigousRow = false;
            Coordinate oldLocation = displacement.getOldLocation();
            Position position = move.getPositionBefore();
            for(Moveable otherMoveable : position.getMoveables(moveable.getColor())) {
                if(otherMoveable==moveable) { continue; }
                if(otherMoveable.getClass()!=moveable.getClass()) { continue; }
                if(!position.canBeReachedBy(newLocation, otherMoveable)) { continue; }

                Coordinate otherLocation = position.getLocation(otherMoveable);
                ambigousColumn |= otherLocation.getColumn()==oldLocation.getColumn();
                ambigousRow |= otherLocation.getRow()==oldLocation.getRow();
                isAmbigous |= true;
            }

            if(isAmbigous) {
                if(ambigousRow || !ambigousColumn) { ambiguousStr += ((char)('a'+oldLocation.getColumn())); }
                if(ambigousColumn) { ambiguousStr += ""+(oldLocation.getRow()+1); }
            }
        }

        String promotionStr = "";
        if(move.isPromotionNeeded()) {
            Character promotionChar = ENTITY_MAPPING.getKey(move.getPromotion().getClass());
            promotionStr = PROMOTION_SEPARATOR+""+promotionChar;
        }

        return moveableStr+ambiguousStr+(isCapture ? CAPTURE_SEPARATOR : "")+
               newLocationStr+promotionStr+(isOppositeKingInCheck ? CHECK : "");
    }
}
