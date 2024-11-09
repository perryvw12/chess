package client;

import static ui.EscapeSequences.WHITE_KING;

public class Repl {
    ChessClientPreLogin client;

    public Repl(String serverUrl) {
        client = new ChessClientPreLogin(serverUrl);
    }

    public void run() {
        System.out.printf("%s Welcome to Chess. Sign in to start %s%n", WHITE_KING, WHITE_KING);
    }
}
