
package ch.astorm.jchess.io;

import ch.astorm.jchess.JChessGame;
import ch.astorm.jchess.core.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads PGN files.
 * Note this reader only supports basic PGN files. Comments and deviations are not
 * supported.
 */
public class PGNReader extends BufferedReader {
    private final List<String> buffer = new ArrayList<>();

    /**
     * Exception thrown when a game cannot be replayed by {@link JChessGame}.
     */
    public static class PGNReaderException extends RuntimeException {
        private JChessGame game;
        private List<String> moves;

        public PGNReaderException(Throwable cause, JChessGame game, List<String> moves) {
            super(cause);
            this.game = game;
            this.moves = moves;
        }

        /**
         * Returns the game that failed.
         */
        public JChessGame getGame() {
            return game;
        }

        /**
         * Returns the moves of the game.
         */
        public List<String> getMoves() {
            return moves;
        }
    }

    /**
     * Creates a new {@code PGNReader} from the specified {@code reader}.
     */
    public PGNReader(Reader reader) {
        super(reader);
    }

    /**
     * Returns the next {@link JChessGame} in the PGN file. If there is no more game,
     * then null will be returned.
     *
     * @return The next game or null.
     */
    public JChessGame readGame() throws IOException {
        Map<String, String> metadata = readMetadata();
        String moves = readMoves();
        if(moves==null) { return null; }

        List<String> parsedMoves = parseMoves(moves);

        JChessGame game = JChessGame.newGame();
        game.getMetadata().putAll(metadata);
        MoveParser moveParser = new MoveParser(game);

        try { moveParser.doMoves(parsedMoves); }
        catch(Exception e) { throw new PGNReaderException(e, game, parsedMoves); }

        if(moves.endsWith("1-0")) { game.resign(Color.BLACK); }
        else if(moves.endsWith("0-1")) { game.resign(Color.WHITE); }
        else if(moves.endsWith("1/2-1/2")) { game.draw(); }
        else if(moves.endsWith("*")) { /* unknown */ }
        else { throw new IllegalStateException("Unknown end game status"); }

        return game;
    }

    private String nextLine() throws IOException {
        if(!buffer.isEmpty()) { return buffer.remove(0); }
        
        String line = readLine();
        while(line!=null && line.trim().isEmpty()) { line = readLine(); }
        return line;
    }

    /**
     * Reads the next metadata line.
     * Returns null if the next line is not a metadata, however it does not mean
     * that EOF has been reached.
     */
    protected String readMetadataLine() throws IOException {
        String line = nextLine();
        if(line==null) { return null; }
        if(line.startsWith("[")) { return line; }

        buffer.add(line);
        return null;
    }

    /**
     * Returns the metadata of the next game.
     * If there is no metadata or EOF has been reached, an empty map is returned.
     */
    protected Map<String, String> readMetadata() throws IOException {
        Map<String, String> metadata = new HashMap<>();

        String metadataLine = readMetadataLine();
        while(metadataLine!=null) {
            int firstQuote = metadataLine.indexOf('\"');
            int lastQuote = metadataLine.indexOf('\"', firstQuote+1);

            String key = metadataLine.substring(1, firstQuote).trim();
            String value = metadataLine.substring(firstQuote+1, lastQuote);
            metadata.put(key, value);

            metadataLine = readMetadataLine();
        }

        return metadata;
    }

    /**
     * Reads the moves.
     * Returns null if there is no more move to read, meaning that EOF has been
     * reached.
     */
    protected String readMoves() throws IOException {
        StringBuilder moves = new StringBuilder(256);
        String moveLine = nextLine();
        while(moveLine!=null) {
            if(moveLine.startsWith("[") || (moveLine.startsWith("1.") && moves.length()>0)) {
                buffer.add(moveLine);
                break;
            }

            moves.append(moveLine.trim()).append(" ");
            moveLine = nextLine();
        }
        return moves.length()>0 ? moves.toString().trim() : null;
    }

    /**
     * Parses the read moves in tokens to feed the {@link MoveParser}.
     */
    protected List<String> parseMoves(String moves) {
        if(!moves.startsWith("1.")) { throw new IllegalArgumentException("Invalid moves: "+moves); }

        List<String> parsedMoves = new ArrayList<>(64);
        int moveCounter = 1;
        String nextMoveStr = moveCounter+".";
        int nextMove = moves.indexOf(nextMoveStr);
        while(nextMove>=0) {
            int currentMove = nextMove;
            int moveLength = nextMoveStr.length();

            ++moveCounter;
            nextMoveStr = moveCounter+".";
            nextMove = moves.indexOf(nextMoveStr);

            String whiteBlackMove = moves.substring(currentMove+moveLength, nextMove<0 ? moves.length() : nextMove).trim();
            int firstSpace = whiteBlackMove.indexOf(' ');
            int secondSpace = whiteBlackMove.indexOf(' ', firstSpace+1);

            String whiteMove = whiteBlackMove.substring(0, firstSpace).trim();
            parsedMoves.add(whiteMove);

            String blackMove = whiteBlackMove.substring(firstSpace+1, secondSpace<0 ? whiteBlackMove.length() : secondSpace).trim();
            if(!blackMove.isEmpty()) { parsedMoves.add(blackMove); }
        }

        return parsedMoves;
    }
}
