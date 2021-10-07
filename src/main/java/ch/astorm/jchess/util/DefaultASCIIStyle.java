
package ch.astorm.jchess.util;

import ch.astorm.jchess.core.Color;
import ch.astorm.jchess.core.Coordinate;
import ch.astorm.jchess.core.Moveable;
import ch.astorm.jchess.core.entities.Bishop;
import ch.astorm.jchess.core.entities.King;
import ch.astorm.jchess.core.entities.Knight;
import ch.astorm.jchess.core.entities.Pawn;
import ch.astorm.jchess.core.entities.Queen;
import ch.astorm.jchess.core.entities.Rook;

/*
     ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
     :         :::::::::    _    :::www:::   _+_   ::::_::::         ::::::::::
  _  :  |=|=|  :: _,, ::   (/)   :::)#(:::   )#(   :::(/):::   _,,   ::|=|=|:::
 (_) :   |#|   ::"- \~::   |#|   :::|#|:::   |#|   :::|#|:::  "- \~  :::|#|::::
 (_) :   |#|   :::|#|:::   |#|   :::|#|:::   |#|   :::|#|:::   |#|   :::|#|::::
     :  /###\  ::/###\::  /###\  ::/###\::  /###\  ::/###\::  /###\  ::/###\:::
     ::::::::::         :::::::::         :::::::::         :::::::::         :
  __ :::::():::    ()   ::::():::    ()   ::::():::    ()   ::::():::    ()   :
   / :::::)(:::    )(   ::::)(:::    )(   ::::)(:::    )(   ::::)(:::    )(   :
  /  ::::/##\::   /##\  :::/##\::   /##\  :::/##\::   /##\  :::/##\::   /##\  :
     ::::::::::         :::::::::         :::::::::         :::::::::         :
     :         :::::::::         :::::::::         :::::::::         ::::::::::
     :         :::::::::         :::::::::         :::::::::         ::::::::::
  /  :         :::::::::         :::::::::         :::::::::         ::::::::::
 (_) :         :::::::::         :::::::::         :::::::::         ::::::::::
     :         :::::::::         :::::::::         :::::::::         ::::::::::
     ::::::::::         :::::::::         :::::::::         :::::::::         :
  _  ::::::::::         :::::::::         :::::::::         :::::::::         :
 |_  ::::::::::         :::::::::         :::::::::         :::::::::         :
  _) ::::::::::         :::::::::         :::::::::         :::::::::         :
     ::::::::::         :::::::::         :::::::::         :::::::::         :
     :         :::::::::         :::::::::         :::::::::         ::::::::::
   . :         :::::::::         :::::::::         :::::::::         ::::::::::
  /| :         :::::::::         :::::::::         :::::::::         ::::::::::
 '-| :         :::::::::         :::::::::         :::::::::         ::::::::::
     :         :::::::::         :::::::::         :::::::::         ::::::::::
     ::::::::::         :::::::::         :::::::::         :::::::::         :
  _  ::::::::::         :::::::::         :::::::::         :::::::::         :
  _) ::::::::::         :::::::::         :::::::::         :::::::::         :
  _) ::::::::::         :::::::::         :::::::::         :::::::::         :
     ::::::::::         :::::::::         :::::::::         :::::::::         :
     :         :::::::::         :::::::::         :::::::::         ::::::::::
  _  :    ()   ::::():::    ()   ::::():::    ()   ::::():::    ()   ::::()::::
   ) :    )(   ::::)(:::    )(   ::::)(:::    )(   ::::)(:::    )(   ::::)(::::
  /_ :   /__\  :::/__\::   /__\  :::/__\::   /__\  :::/__\::   /__\  :::/__\:::
     :         :::::::::         :::::::::         :::::::::         ::::::::::
     ::::::::::         ::::_::::   www   :::_+_:::    _    :::::::::         :
     :::|_|_|::   _,,   :::(/):::   ) (   :::) (:::   (/)   :: _,, ::  |_|_|  :
  /| ::::| |:::  "- \~  :::| |:::   | |   :::| |:::   | |   ::"- \~::   | |   :
   | ::::| |:::   | |   :::| |:::   | |   :::| |:::   | |   :::| |:::   | |   :
     :::/___\::  /___\  ::/___\::  /___\  ::/___\::  /___\  ::/___\::  /___\  :
     ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
                   _        _        _        __       __       _              
         /\       |_)      /        | \      |_       |_       /        |_|    
        /--\      |_)      \_       |_/      |__      |        \_?      | |    
*/

/**
 * Default simple {@code ASCIIStyle}.
 */
public class DefaultASCIIStyle implements ASCIIStyle {

    /*
    Dots (.) will be replaced by spaces.
    Arobases (@) will be replaced by sharps.
    */

    private static final String[] WHITE_PAWN = new String[]
    {
        "         ",
        "    ()   ",
        "    )(   ",
        "   /__\\  ",
        "         "
    };
    private static final String[] BLACK_PAWN = new String[]
    {
        "         ",
        "    ()   ",
        "    )(   ",
        "   /@@\\  ",
        "         "
    };
    private static final String[] WHITE_ROOK = new String[]
    {
        "         ",
        "  |_|_|  ",
        "   |.|   ",
        "   |.|   ",
        "  /___\\  ",
        "         "
    };
    private static final String[] BLACK_ROOK = new String[]
    {
        "         ",
        "  |=|=|  ",
        "   |@|   ",
        "   |@|   ",
        "  /@@@\\  ",
        "         "
    };
    private static final String[] WHITE_KNIGHT = new String[]
    {
        "         ",
        "  ._,,.  ",
        "  \"-.\\~  ",
        "   |.|   ",
        "  /___\\  "
    };
    private static final String[] BLACK_KNIGHT = new String[]
    {
        "         ",
        "  ._,,.  ",
        "  \"-.\\~  ",
        "   |@|   ",
        "  /@@@\\  "
    };
    private static final String[] WHITE_BISHOP = new String[]
    {
        "    _    ",
        "   (/)   ",
        "   |.|   ",
        "   |.|   ",
        "  /___\\  "
    };
    private static final String[] BLACK_BISHOP = new String[]
    {
        "    _    ",
        "   (/)   ",
        "   |@|   ",
        "   |@|   ",
        "  /@@@\\  "
    };
    private static final String[] WHITE_QUEEN = new String[]
    {
        "   www   ",
        "   ).(   ",
        "   |.|   ",
        "   |.|   ",
        "  /___\\  "
    };
    private static final String[] BLACK_QUEEN = new String[]
    {
        "   www   ",
        "   )@(   ",
        "   |@|   ",
        "   |@|   ",
        "  /@@@\\  "
    };
    private static final String[] WHITE_KING = new String[]
    {
        "   _+_   ",
        "   ).(   ",
        "   |.|   ",
        "   |.|   ",
        "  /___\\  "
    };
    private static final String[] BLACK_KING = new String[]
    {
        "   _+_   ",
        "   )@(   ",
        "   |@|   ",
        "   |@|   ",
        "  /@@@\\  "
    };

    @Override
    public void renderCell(char[][] cell, Coordinate cellCoordinate, Moveable moveable) {
        String[] renderData = new String[0];
        if(moveable instanceof Pawn) { renderData = moveable.getColor()==Color.WHITE ? WHITE_PAWN : BLACK_PAWN; }
        else if(moveable instanceof Rook) { renderData = moveable.getColor()==Color.WHITE ? WHITE_ROOK : BLACK_ROOK; }
        else if(moveable instanceof Knight) { renderData = moveable.getColor()==Color.WHITE ? WHITE_KNIGHT : BLACK_KNIGHT; }
        else if(moveable instanceof Bishop) { renderData = moveable.getColor()==Color.WHITE ? WHITE_BISHOP : BLACK_BISHOP; }
        else if(moveable instanceof Queen) { renderData = moveable.getColor()==Color.WHITE ? WHITE_QUEEN : BLACK_QUEEN; }
        else if(moveable instanceof King) { renderData = moveable.getColor()==Color.WHITE ? WHITE_KING : BLACK_KING; }

        for(int i=0 ; i<renderData.length ; ++i) {
            String line = renderData[i];
            for(int c=0 ; c<line.length() ; ++c) {
                char pc = line.charAt(c);
                if(pc=='.') { cell[i][c] = ' '; }
                else if(pc=='@') { cell[i][c] = '#'; }
                else if(pc!=' ') { cell[i][c] = pc; }
            }
        }
    }
}
