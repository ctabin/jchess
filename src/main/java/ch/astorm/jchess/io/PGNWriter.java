
package ch.astorm.jchess.io;

import ch.astorm.jchess.JChessGame;
import ch.astorm.jchess.JChessGame.Status;
import ch.astorm.jchess.core.Move;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Writes games in PGN format.
 */
public class PGNWriter extends PrintWriter {
    public PGNWriter(Writer writer) {
        super(writer);
    }

    /**
     * Writes the specified {@code game}.
     */
    public void writeGame(JChessGame game) {
        String result;
        Status status = game.getStatus();
        if(status==Status.NOT_FINISHED) { result = "*"; }
        else if(status==Status.WIN_WHITE) { result = "1-0"; }
        else if(status==Status.WIN_BLACK) { result = "0-1"; }
        else { result = "1/2-1/2"; }

        Map<String,String> mandatoryHeaders = new LinkedHashMap<>();
        mandatoryHeaders.put("Event", "");
        mandatoryHeaders.put("Site", "");
        mandatoryHeaders.put("Date", "????.??.??");
        mandatoryHeaders.put("Round", "");
        mandatoryHeaders.put("White", "");
        mandatoryHeaders.put("Black", "");
        mandatoryHeaders.put("Result", result);

        for(Entry<String,String> entry : game.getMetadata().entrySet()) {
            String key = entry.getKey();
            if(key.equalsIgnoreCase("Result")) { continue; }

            mandatoryHeaders.remove(key);
            String value = entry.getValue();
            String header = "["+key+" \""+value.replace("\"", "'")+"\"]\n";
            write(header);
        }

        mandatoryHeaders.forEach((k,v) -> write("["+k+" \""+v+"\"]\n"));
        write("\n");

        int moveCounter = 1;
        StringBuilder builder = new StringBuilder(80);
        Iterator<Move> moveIterator = game.getPosition().getMoveHistory().iterator();
        while(moveIterator.hasNext()) {
            Move whiteMove = moveIterator.next();

            String moveNumberStr = moveCounter+".";
            String whiteMoveStr = MoveParser.getMoveString(whiteMove);
            String fullMoveStr = moveNumberStr+whiteMoveStr+" ";

            if(moveIterator.hasNext()) {
                Move blackMove = moveIterator.next();
                String blackMoveStr = MoveParser.getMoveString(blackMove);
                fullMoveStr += blackMoveStr;
            }

            if(builder.length()+fullMoveStr.length()+1>80) {
                write(builder.toString());
                write("\n");

                builder = new StringBuilder(80);
            } else if(builder.length()>0) {
                builder.append(" ");
            }
            builder.append(fullMoveStr);

            ++moveCounter;
        }

        if(builder.length()>0) {
            write(builder.toString());
        }

        write(" "+result);
        write("\n\n");
    }
}
