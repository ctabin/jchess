
package ch.astorm.jchess.core;

import ch.astorm.jchess.core.rules.Displacement;
import ch.astorm.jchess.io.MoveParser;
import java.util.List;

/**
 * Represents a move.
 */
public class Move {
    private Position beforeMove;
    private Position afterMove;
    private Displacement displacement;
    private List<Displacement> linkedDisplacements;
    private Moveable capturedEntity;
    private Moveable promoteTo;
    private boolean promotionNeeded;

    /**
     * Creates a simple move.
     *
     * @param position The position.
     * @param displacement The displacement.
     */
    public Move(Position position, Displacement displacement) {
        this(position, displacement, null, null);
    }

    /**
     * Creates a simple move with a capture.
     *
     * @param position The position.
     * @param displacement The displacement.
     * @param captured The captured {@link Moveable}.
     */
    public Move(Position position, Displacement displacement, Moveable captured) {
        this(position, displacement, null, captured);
    }

    /**
     * Creates a multi-displacement move (castling).
     *
     * @param position The position.
     * @param displacement The main displacement.
     * @param linked The linked displacements or null.
     */
    public Move(Position position, Displacement displacement, List<Displacement> linked) {
        this(position, displacement, linked, null);
    }

    private Move(Position position, Displacement displacement, List<Displacement> linked, Moveable captured) {
        this.beforeMove = position;
        this.displacement = displacement;
        this.linkedDisplacements = linked;
        this.capturedEntity = captured;
    }

    /**
     * Returns the {@link Position} before the move.
     */
    public Position getPositionBefore() {
        return beforeMove;
    }

    /**
     * Returns the {@link Position} after the move.
     * This value will be only available once the {@link #apply(ch.astorm.jchess.core.Position)}
     * method has been invoked.
     */
    public Position getPositionAfter() {
        return afterMove;
    }

    /**
     * Returns the main displacement of the move.
     */
    public Displacement getDisplacement() {
        return displacement;
    }

    /**
     * Returns the linked displacements of the move.
     * Only special moves such as castling have linked displacements and in a regular
     * chess game, there could be only up to one linked displacement.
     */
    public List<Displacement> getLinkedDisplacements() {
        return linkedDisplacements;
    }

    /**
     * Returns the {@link Moveable} entity that has been captured or null if there
     * is none.
     * <p>Note that the captured entity can be on a different square than the new
     * position of the moved entity, typically when a pawn is taken en-passant.</p>
     */
    public Moveable getCapturedEntity() {
        return capturedEntity;
    }

    /**
     * Returns the transformation.
     */
    public Moveable getPromotion() {
        return promoteTo;
    }

    /**
     * Defines the transformation to apply.
     * This is typically when a pawn reaches the opposite final row and get promoted.
     */
    public void setPromotion(Moveable promotion) {
        this.promoteTo = promotion;
    }

    /**
     * Returns true if a promotion has to be defined.
     */
    public boolean isPromotionNeeded() {
        return promotionNeeded;
    }

    /**
     * Defines if a promotion has to be defined in this move.
     */
    public void setPromotionNeeded(boolean promotion) {
        this.promotionNeeded = promotion;
    }

    /**
     * Applies this move on the specified {@code position}.
     * Note that this method should never be called directly, instead use the
     * {@link Position#apply(ch.astorm.jchess.moves.Move)} method.
     */
    protected void apply(Position position) {
        Moveable promoteTo = getPromotion();
        if(promotionNeeded && promoteTo==null) { throw new IllegalStateException("No promotion has been set"); }
        if(!promotionNeeded && promoteTo!=null) { throw new IllegalStateException("A promotion has been set"); }

        if(capturedEntity!=null) {
            Coordinate capturedLocation = position.getLocation(capturedEntity);
            position.put(capturedLocation, null);
        }

        position.put(displacement.getOldLocation(), null);
        position.put(displacement.getNewLocation(), promoteTo==null ? displacement.getMoveable() : promoteTo);

        if(linkedDisplacements!=null) {
            for(Displacement disp : linkedDisplacements) {
                position.put(disp.getNewLocation(), disp.getMoveable());
            }
        }

        afterMove = position;
    }

    /**
     * Returns this {@code Move} in algebraic notation.
     * @see MoveParser#getMoveString(ch.astorm.jchess.core.Move)
     */
    public String toString() {
        return MoveParser.getMoveString(this);
    }
}
