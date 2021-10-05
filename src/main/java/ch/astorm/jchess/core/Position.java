
package ch.astorm.jchess.core;

import ch.astorm.jchess.core.entities.King;
import ch.astorm.jchess.core.entities.Queen;
import ch.astorm.jchess.core.rules.DisplacementRule;
import ch.astorm.jchess.core.rules.RuleManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

/**
 * Represents a position on a {@link Board}.
 */
public class Position {
    private final Board board;
    private final RuleManager ruleManager;
    private final Color colorToMove;
    private final BidiMap<Coordinate, Moveable> moveables;
    private final Map<Moveable, MoveableProperties> moveableProperties;
    private final List<Move> moveHistory;
    private List<Move> availableLegalMoves;
    private Position previousPosition;

    private static class MoveableProperties {
        int nbMoves;
    }

    /**
     * Creates a new position.
     *
     * @param board The {@link Board}.
     * @param ruleManager The {@link RuleManager}.
     * @param colorToMove The color to move.
     */
    public Position(Board board, RuleManager ruleManager, Color colorToMove) {
        this.board = board;
        this.ruleManager = ruleManager;
        this.colorToMove = colorToMove;
        this.moveables = new DualHashBidiMap<>();
        this.moveableProperties = new HashMap<>();
        this.moveHistory = new ArrayList<>(128);
    }

    /**
     * Returns the {@code Board}.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Returns the {@link Color} that have the move.
     */
    public Color getColorToMove() {
        return colorToMove;
    }

    /**
     * Updates the internal cache of accessible coordinates.
     */
    private void computeLegalMoves() {
        if(availableLegalMoves!=null) { return; }

        Color oppositeColor = colorToMove.opposite();
        King king = null;
        for(Entry<Coordinate, Moveable> entry : moveables.entrySet()) {
            Moveable moveable = entry.getValue();
            if(moveable.getColor()==colorToMove && moveable instanceof King) {
                if(king!=null) { throw new IllegalStateException("Multiple "+colorToMove+" king in position"); }
                king = (King)moveable;
            }
        }

        List<Move> legalMoves = new ArrayList<>(42);
        for(Entry<Coordinate, Moveable> entry : moveables.entrySet()) {
            Moveable moveable = entry.getValue();
            if(moveable.getColor()!=colorToMove) { continue; }

            Coordinate location = entry.getKey();
            DisplacementRule<Moveable> rule = ruleManager.getDisplacementRule(moveable);
            List<Move> allMoves = rule.getAvailableMoves(this, location, moveable);
            if(king!=null) {
                for(Move move : allMoves) {
                    if(move.isPromotionNeeded()) { move.setPromotion(new Queen(colorToMove)); }

                    Position checkPosition = apply(move);
                    Coordinate kingLocation = checkPosition.getLocation(king);
                    if(!checkPosition.canBeReached(kingLocation, oppositeColor)) {
                        legalMoves.add(move);
                    }

                    move.setPromotion(null);
                }
            } else {
                legalMoves.addAll(allMoves);
            }
        }

        availableLegalMoves = legalMoves;
    }

    /**
     * Clears the accessible coordinates cache.
     */
    private void clearCache() {
        availableLegalMoves = null;
    }

    /**
     * Returns all the legal moves available in this position for the color to play.
     */
    public List<Move> getLegalMoves() {
        computeLegalMoves();
        return Collections.unmodifiableList(availableLegalMoves);
    }

    /**
     * Returns the move history to reach this position.
     */
    public List<Move> getMoveHistory() {
        return Collections.unmodifiableList(moveHistory);
    }

    /**
     * Returns the previous position or null if no move has been done.
     */
    public Position getPreviousPosition() {
        return previousPosition;
    }

    /**
     * Applies the move and returns a new {@code Position} with the move applied and the
     * opposite color to play. The current position is left untouched.
     *
     * @param move The move to apply.
     * @return A new {@code Position} with the applied move.
     * @see Move#apply(ch.astorm.jchess.core.Position)
     */
    public Position apply(Move move) {
        Position p = new Position(board, ruleManager, colorToMove.opposite());
        p.moveables.putAll(moveables);
        p.moveHistory.addAll(moveHistory);
        p.previousPosition = this;
        
        for(Entry<Moveable, MoveableProperties> entry : moveableProperties.entrySet()) {
            Moveable m = entry.getKey();
            MoveableProperties c = entry.getValue();
            p.moveableProperties.put(m, c);
        }

        move.apply(p);
        p.moveHistory.add(move);

        //updates displacements count
        p.increaseDisplacementCount(move.getDisplacement().getMoveable(), 1);
        if(move.getLinkedDisplacements()!=null) { move.getLinkedDisplacements().forEach(d -> p.increaseDisplacementCount(d.getMoveable(), 1)); }

        return p;
    }

    /**
     * Returns true if the {@code location} can be reached by any {@link Moveable} entity
     * of the specified {@code color}.
     * Note that this method doesn't check the validity of the move (for instance if
     * the moveable is pinned).
     *
     * @param location The location.
     * @param color The color.
     * @return True if there is at least one entity that can reach the location.
     */
    public boolean canBeReached(Coordinate location, Color color) {
        for(Entry<Coordinate, Moveable> entry : moveables.entrySet()) {
            Moveable moveable = entry.getValue();
            if(moveable.getColor()!=color) { continue; }

            Coordinate currentLocation = entry.getKey();
            DisplacementRule<Moveable> rule = ruleManager.getDisplacementRule(moveable);
            if(rule.canAccess(this, currentLocation, moveable, location)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true if the {@code location} can be reached by the specified {@code moveable}.
     *
     * @param location The location.
     * @param moveable The {@link Moveable} entity.
     * @return True if {@code moveable} can move to {@code location} from its current location.
     */
    public boolean canBeReachedBy(Coordinate location, Moveable moveable) {
        Coordinate currentLocation = getLocation(moveable);
        return canBeReachedBy(location, currentLocation, moveable);
    }

    private boolean canBeReachedBy(Coordinate location, Coordinate currentLocation, Moveable moveable) {
        DisplacementRule<Moveable> rule = ruleManager.getDisplacementRule(moveable);
        return rule.canAccess(this, currentLocation, moveable, location);
    }

    /**
     * Returns all the {@link Moveable} entities in the position.
     */
    public Map<Coordinate, Moveable> getMoveables() {
        return Collections.unmodifiableMap(moveables);
    }

    /**
     * Returns all the {@link Moveable} entities of the specified {@code color}.
     */
    public List<Moveable> getMoveables(Color color) {
        return moveables.entrySet().stream().
                map(e -> e.getValue()).
                filter(m -> m.getColor()==color).
                collect(Collectors.toList());
    }

    /**
     * Returns the {@link Moveable} entity at the specified location or null if there
     * is none.
     *
     * @param algebraicCoordinate The coordinate (eg 'e5').
     * @return The {@link Moveable} or null.
     */
    public Moveable get(String algebraicCoordinate) {
        return get(new Coordinate(algebraicCoordinate));
    }

    /**
     * Returns the {@link Moveable} entity at the specified location or null if there
     * is none.
     *
     * @param row The row index (zero-based).
     * @param column The column index (zero-based).
     * @return The {@link Moveable} or null.
     */
    public Moveable get(int row, int column) {
        return get(new Coordinate(row, column));
    }

    /**
     * Returns the {@link Moveable} entity at the specified location or null if there
     * is none.
     *
     * @param location The location.
     * @return The {@link Moveable} or null.
     */
    public Moveable get(Coordinate location) {
        checkCoordinate(location);
        return moveables.get(location);
    }

    /**
     * Puts the given {@code moveable} at the specified coordinates.
     *
     * @param algebraicCoordinate The coordinate (eg 'e5').
     * @param moveable The moveable or null to remove the entity.
     * @return The removed {@code Moveable} at the coordinates.
     */
    public Moveable put(String algebraicCoordinate, Moveable moveable) {
        return put(new Coordinate(algebraicCoordinate), moveable);
    }

    /**
     * Puts the given {@code moveable} at the specified coordinates.
     *
     * @param row The row index (zero-based).
     * @param column The column index (zero-based).
     * @param moveable The moveable or null to remove the entity.
     * @return The removed {@code Moveable} at the coordinates.
     */
    public Moveable put(int row, int column, Moveable moveable) {
        return put(new Coordinate(row, column), moveable);
    }

    /**
     * Puts the given {@code moveable} at the specified coordinates.
     * If the {@code moveable} was already present in the position, it is removed
     * from its previous location.
     *
     * @param location The location.
     * @param moveable The moveable or null to remove the entity.
     * @return The removed {@code Moveable} at the coordinates.
     */
    public Moveable put(Coordinate location, Moveable moveable) {
        checkCoordinate(location);
        clearCache();
        if(moveable==null) { return moveables.remove(location); }
        else {
            moveables.removeValue(moveable);
            return moveables.put(location, moveable);
        }
    }

    private void checkCoordinate(Coordinate coordinate) {
        if(!board.isValid(coordinate)) {
            throw new IllegalArgumentException("Invalid coordinate: "+coordinate.getRow()+"x"+coordinate.getColumn());
        }
    }

    /**
     * Returns the position of the specified {@code moveable} entity.
     *
     * @param moveable The {@code moveable} entity.
     * @return The coordinate or null.
     */
    public Coordinate getLocation(Moveable moveable) {
        return moveables.getKey(moveable);
    }

    /**
     * Returns the location of the first {@link Moveable} matching the {@code clazz}
     * and {@code color}.
     *
     * @param clazz The class to match.
     * @param color The color to match.
     * @return The location or null if not present in the position.
     */
    public Coordinate findLocation(Class<? extends Moveable> clazz, Color color) {
        for(Entry<Coordinate, Moveable> entry : moveables.entrySet()) {
            Moveable moveable = entry.getValue();
            if(clazz.isAssignableFrom(moveable.getClass()) && moveable.getColor()==color) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Returns the number of displacements of {@code moveable}.
     */
    public int getDisplacementCount(Moveable moveable) {
        MoveableProperties prop = moveableProperties.get(moveable);
        return prop!=null ? prop.nbMoves : 0;
    }

    /**
     * Increases the displacement count of {@code moveable} by {@code value}.
     *
     * @param moveable The {@link Moveable} entity.
     * @param value The increment value.
     */
    public void increaseDisplacementCount(Moveable moveable, int value) {
        MoveableProperties mp = moveableProperties.get(moveable);
        if(mp==null) {
            mp = new MoveableProperties();
            moveableProperties.put(moveable, mp);
        }
        mp.nbMoves = mp.nbMoves+value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        for(Entry<Coordinate, Moveable> entry : moveables.entrySet()) {
            hash = 53 * hash * Objects.hashCode(entry.getValue());
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Position other = (Position) obj;
        if(other.moveables.size()!=moveables.size()) {
            return false;
        }

        for(Entry<Coordinate, Moveable> entry : moveables.entrySet()) {
            Moveable moveable = entry.getValue();
            Moveable omoveable = other.get(entry.getKey());
            if(omoveable==null ||
               !moveable.getClass().equals(omoveable.getClass()) ||
               moveable.getColor()!=omoveable.getColor()) {
                return false;
            }
        }

        return true;
    }
}
