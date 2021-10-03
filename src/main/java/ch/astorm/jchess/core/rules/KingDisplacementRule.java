
package ch.astorm.jchess.core.rules;

import ch.astorm.jchess.core.Board;
import ch.astorm.jchess.core.Color;
import ch.astorm.jchess.core.Coordinate;
import ch.astorm.jchess.core.Moveable;
import ch.astorm.jchess.core.Position;
import ch.astorm.jchess.core.entities.King;
import ch.astorm.jchess.core.entities.Rook;
import ch.astorm.jchess.core.Move;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Displacement rules of a King.
 */
public class KingDisplacementRule extends SimpleDisplacementRule<King> {

    public KingDisplacementRule() {
        withVerticalHorizontalLookups(1);
        withDiagonalLookups(1);
    }

    @Override
    public List<Move> getAvailableMoves(Position position, Coordinate location, King king) {
        List<Move> moves = super.getAvailableMoves(position, location, king);

        //filters out the moves where the king moves into check
        Color oppositeColor = king.getColor().opposite();
        moves = moves.stream().
                    filter(m -> !position.canBeReached(m.getDisplacement().getNewLocation(), oppositeColor)).
                    collect(Collectors.toList());

        int nbKingDisplacement = position.getDisplacementCount(king);
        if(nbKingDisplacement==0) {
            Move smallCastling = getCastlingMove(position, location, king, Board.DEFAULT_ROWS-1, 1);
            if(smallCastling!=null) { moves.add(smallCastling); }

            Move bigCastling = getCastlingMove(position, location, king, 0, -1);
            if(bigCastling!=null) { moves.add(bigCastling); }
        }

        return moves;
    }

    private Move getCastlingMove(Position position, Coordinate location, King king, int rookColumn, int direction) {
        int kingRow = location.getRow();
        int kingColumn = location.getColumn();

        Coordinate rookLocation = new Coordinate(kingRow, rookColumn);
        Moveable rook = position.get(rookLocation);
        if(rook==null || !(rook instanceof Rook)) { return null; }
        if(position.getDisplacementCount(rook)>0) { return null; }

        Color oppositeColor = king.getColor().opposite();
        for(int i=1 ; i<=3 ; ++i) {
            Coordinate inBetween = new Coordinate(kingRow, kingColumn+(direction*i));
            if(i<3 || direction<0) {
                Moveable entityInBetween = position.get(inBetween);
                if(entityInBetween!=null) { return null; }
            }

            if(i<3 && position.canBeReached(inBetween, oppositeColor)) {
                return null;
            }
        }

        Coordinate newKingLocation = new Coordinate(kingRow, kingColumn+(2*direction));
        return new Move(new Displacement(king, location, newKingLocation),
                        Arrays.asList(new Displacement(rook, rookLocation, new Coordinate(kingRow, newKingLocation.getColumn()-(1*direction)))));
    }
}
