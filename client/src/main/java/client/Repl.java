package client;

import server.ServerFacade;

import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    ServerFacade server;
    String clientState = "loggedOut";

    public Repl(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        ChessClientPreLogin client = new ChessClientPreLogin(server);
        System.out.printf("%s Welcome to Chess. Sign in to start %s%n", WHITE_KING, WHITE_KING);
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
                if(client.getAuth() != null) {
                    clientState = "loggedIn";
                    postLoginRun(client.getAuth());
                    client.logout();
                    System.out.print(client.help());
                }
            } catch (Throwable ex) {
                var msg = ex.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void postLoginRun(String authToken) {
        ChessClientPostLogin client = new ChessClientPostLogin(server, authToken);
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (Objects.equals(clientState, "loggedIn")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
                if(client.getAuth() == null) {
                    clientState = "loggedOut";
                }
                if(client.isPlaying()) {

                }
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
