package client;
import server.ServerFacade;

import java.util.Arrays;

public class ChessClientPreLogin {
    String serverUrl;
    ServerFacade server;

    public ChessClientPreLogin(String serverUrl) {
        this.serverUrl = serverUrl;
        server = new ServerFacade();
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                case "help" -> help();
            };
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) {

    }
}
