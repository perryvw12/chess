package client;

import server.ServerFacade;
import java.util.Scanner;
import static ui.EscapeSequences.*;

public class AltRepl {
    ServerFacade server;

    public AltRepl(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        ChessClient client = new ChessClient(server);
        System.out.printf("%s Welcome to Chess. Sign in to start %s%n", WHITE_KING, WHITE_KING);
        System.out.print(client.preHelp());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable ex) {
                var msg = ex.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }

}