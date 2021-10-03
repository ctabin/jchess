
package ch.astorm.jchess.core.rules;

import ch.astorm.jchess.core.Board;
import ch.astorm.jchess.core.Color;
import ch.astorm.jchess.core.Coordinate;
import ch.astorm.jchess.core.Moveable;
import ch.astorm.jchess.core.Position;
import ch.astorm.jchess.core.entities.Pawn;
import ch.astorm.jchess.core.Move;
import java.util.ArrayList;
import java.util.List;

/**
 * Displacement rules of a pawn.
 */
public class PawnDisplacementRule implements DisplacementRule<Pawn> {

    @Override
    public List<Move> getAvailableMoves(Position position, Coordinate location, Pawn moveable) {
        int direction = getDirection(moveable);

        List<Move> moves = new ArrayList<>();

        Move push1 = getPushMove(position, location, moveable, direction, 1);
        if(push1!=null) { moves.add(push1); }

        int pawnRow = location.getRow();
        if(push1!=null && ((direction>0 && pawnRow==1) || (direction<0 && pawnRow==Board.DEFAULT_ROWS-2))) {
            Move push2 = getPushMove(position, location, moveable, direction, 2);
            if(push2!=null) { moves.add(push2); }
        }

        Move capture1 = getCaptureMove(position, location, moveable, location.to(direction, 1));
        if(capture1!=null) { moves.add(capture1); }

        Move capture2 = getCaptureMove(position, location, moveable, location.to(direction, -1));
        if(capture2!=null) { moves.add(capture2); }

        if(capture1==null) {
            Move captureEnPassant1 = getCaptureEnPassantMove(position, location, moveable, location.to(direction, 1), location.to(0, 1));
            if(captureEnPassant1!=null) { moves.add(captureEnPassant1); }
        }

        if(capture2==null) {
            Move captureEnPassant2 = getCaptureEnPassantMove(position, location, moveable, location.to(direction, -1), location.to(0, -1));
            if(captureEnPassant2!=null) { moves.add(captureEnPassant2); }
        }

        return moves;
    }

    @Override
    public boolean canAccess(Position position, Coordinate location, Pawn moveable, Coordinate target) {
        int direction = getDirection(moveable);
        if(target.getRow()!=location.getRow()+direction) { return false; }

        int diff = target.getColumn()-location.getColumn();
        return diff==1 || diff==-1;
    }

    private Move getCaptureEnPassantMove(Position position, Coordinate location, Pawn pawn, Coordinate afterLocation, Coordinate captureLocation) {
        if(!position.getBoard().isValid(afterLocation)) { return null; }

        Moveable captured = position.get(captureLocation);
        if(captured!=null && captured.getClass()==Pawn.class && captured.getColor()==pawn.getColor().opposite()) {
            List<Move> history = position.getMoveHistory();
            if(history.isEmpty()) { return null; }

            Move lastMove = history.get(history.size()-1);
            if(lastMove.getDisplacement().getMoveable()!=captured) { return null; }
            if(lastMove.getDisplacement().getOldLocation().getRow()!=1 && lastMove.getDisplacement().getOldLocation().getRow()!=Board.DEFAULT_ROWS-2) { return null; }

            return new Move(new Displacement(pawn, location, afterLocation), captured);
        }

        return null;
    }

    private Move getCaptureMove(Position position, Coordinate location, Pawn pawn, Coordinate captureLocation) {
        if(!position.getBoard().isValid(captureLocation)) { return null; }

        Moveable captured = position.get(captureLocation);
        if(captured!=null && captured.getColor()==pawn.getColor().opposite()) {
            return new PawnMove(new Displacement(pawn, location, captureLocation), captured);
        }

        return null;
    }

    private Move getPushMove(Position position, Coordinate location, Pawn pawn, int direction, int mult) {
        Coordinate front = location.to(direction*mult, 0);
        if(!position.getBoard().isValid(front)) { return null; }
        if(position.get(front)==null) {return new PawnMove(new Displacement(pawn, location, front)); }
        return null;
    }

    private int getDirection(Pawn pawn) {
        return pawn.getColor()==Color.WHITE ? 1 : -1;
    }
}

class PawnMove extends Move {
    public PawnMove(Displacement displacement) {
        super(displacement);
        setPromotionFlag();
    }

    public PawnMove(Displacement displacement, Moveable captured) {
        super(displacement, captured);
        setPromotionFlag();
    }

    private void setPromotionFlag() {
        Coordinate newLocation = getDisplacement().getNewLocation();
        int row = newLocation.getRow();
        setPromotionNeeded(row==0 || row==Board.DEFAULT_ROWS-1);
    }
}
