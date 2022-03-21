
package ch.astorm.jchess.util;

import ch.astorm.jchess.JChessGame;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class ASCIIPositionRendererTest {
    private final String separator = System.lineSeparator();
    private final String expectedOutput = "" +
            "     ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::" +  separator +
            "     :         :::::::::    _    :::www:::   _+_   ::::_::::         ::::::::::" + separator +
            "  _  :  |=|=|  :: _,, ::   (/)   :::)#(:::   )#(   :::(/):::   _,,   ::|=|=|:::" + separator +
            " (_) :   |#|   ::\"- \\~::   |#|   :::|#|:::   |#|   :::|#|:::  \"- \\~  :::|#|::::" + separator +
            " (_) :   |#|   :::|#|:::   |#|   :::|#|:::   |#|   :::|#|:::   |#|   :::|#|::::" + separator +
            "     :  /###\\  ::/###\\::  /###\\  ::/###\\::  /###\\  ::/###\\::  /###\\  ::/###\\:::" + separator +
            "     ::::::::::         :::::::::         :::::::::         :::::::::         :" + separator +
            "  __ :::::():::    ()   ::::():::    ()   ::::():::    ()   ::::():::    ()   :" + separator +
            "   / :::::)(:::    )(   ::::)(:::    )(   ::::)(:::    )(   ::::)(:::    )(   :" + separator +
            "  /  ::::/##\\::   /##\\  :::/##\\::   /##\\  :::/##\\::   /##\\  :::/##\\::   /##\\  :" + separator +
            "     ::::::::::         :::::::::         :::::::::         :::::::::         :" + separator +
            "     :         :::::::::         :::::::::         :::::::::         ::::::::::" + separator +
            "     :         :::::::::         :::::::::         :::::::::         ::::::::::" + separator +
            "  /  :         :::::::::         :::::::::         :::::::::         ::::::::::" + separator +
            " (_) :         :::::::::         :::::::::         :::::::::         ::::::::::" + separator +
            "     :         :::::::::         :::::::::         :::::::::         ::::::::::" + separator +
            "     ::::::::::         :::::::::         :::::::::         :::::::::         :" + separator +
            "  _  ::::::::::         :::::::::         :::::::::         :::::::::         :" + separator +
            " |_  ::::::::::         :::::::::         :::::::::         :::::::::         :" + separator +
            "  _) ::::::::::         :::::::::         :::::::::         :::::::::         :" + separator +
            "     ::::::::::         :::::::::         :::::::::         :::::::::         :" + separator +
            "     :         :::::::::         :::::::::         :::::::::         ::::::::::" + separator +
            "   . :         :::::::::         :::::::::         :::::::::         ::::::::::" + separator +
            "  /| :         :::::::::         :::::::::         :::::::::         ::::::::::" + separator +
            " '-| :         :::::::::         :::::::::         :::::::::         ::::::::::" + separator +
            "     :         :::::::::         :::::::::         :::::::::         ::::::::::" + separator +
            "     ::::::::::         :::::::::         :::::::::         :::::::::         :" + separator +
            "  _  ::::::::::         :::::::::         :::::::::         :::::::::         :" + separator +
            "  _) ::::::::::         :::::::::         :::::::::         :::::::::         :" + separator +
            "  _) ::::::::::         :::::::::         :::::::::         :::::::::         :" + separator +
            "     ::::::::::         :::::::::         :::::::::         :::::::::         :" + separator +
            "     :         :::::::::         :::::::::         :::::::::         ::::::::::" + separator +
            "  _  :    ()   ::::():::    ()   ::::():::    ()   ::::():::    ()   ::::()::::" + separator +
            "   ) :    )(   ::::)(:::    )(   ::::)(:::    )(   ::::)(:::    )(   ::::)(::::" + separator +
            "  /_ :   /__\\  :::/__\\::   /__\\  :::/__\\::   /__\\  :::/__\\::   /__\\  :::/__\\:::" + separator +
            "     :         :::::::::         :::::::::         :::::::::         ::::::::::" + separator +
            "     ::::::::::         ::::_::::   www   :::_+_:::    _    :::::::::         :" + separator +
            "     :::|_|_|::   _,,   :::(/):::   ) (   :::) (:::   (/)   :: _,, ::  |_|_|  :" + separator +
            "  /| ::::| |:::  \"- \\~  :::| |:::   | |   :::| |:::   | |   ::\"- \\~::   | |   :" + separator +
            "   | ::::| |:::   | |   :::| |:::   | |   :::| |:::   | |   :::| |:::   | |   :" + separator +
            "     :::/___\\::  /___\\  ::/___\\::  /___\\  ::/___\\::  /___\\  ::/___\\::  /___\\  :" + separator +
            "     ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::" + separator +
            "                   _        _        _        __       __       _              " + separator +
            "         /\\       |_)      /        | \\      |_       |_       /        |_|    " + separator +
            "        /--\\      |_)      \\_       |_/      |__      |        \\_?      | |    " + separator +
            "                                                                               " + separator +
            separator;

    @Test
    public void testRendering() {
        JChessGame game = JChessGame.newGame();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try(PrintStream stream = new PrintStream(baos, true, StandardCharsets.US_ASCII)) {
            ASCIIPositionRenderer.render(stream, game.getPosition());
        }

        String str = baos.toString(StandardCharsets.US_ASCII);
        assertEquals(expectedOutput, str);
    }
}
