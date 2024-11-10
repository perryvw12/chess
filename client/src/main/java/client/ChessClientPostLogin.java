package client;

import exception.ServiceException;
import server.ServerFacade;

import java.util.Arrays;
import java.util.HashMap;

public class ChessClientPostLogin {
    String authToken;
    ServerFacade server;

    public ChessClientPostLogin(ServerFacade server, String authToken) {
        this.authToken = authToken;
        this.server = server;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ServiceException ex) {
            return ex.getMessage();
        }
    }

    public String createGame(String... params) throws ServiceException {
        if (params.length >= 1) {
            var gameName = params[0];
            HashMap<String, String> createGameData = new HashMap<>();
            createGameData.put("authorization", authToken);
            createGameData.put("gameName", gameName);
            server.createGame(createGameData);
            return String.format("Created a game with the name %s", gameName);
        }
        throw new ServiceException(400, "Expected: <NAME>");
    }

    public String listGames() throws ServiceException {
        HashMap<String, String> listGameReq = new HashMap<>();
        listGameReq.put("authorization", authToken);
        var results = server.listGames(listGameReq);
        return results.values().toString();
    }

    public String help() {
        return """
                - create <NAME> - creates a game
                - list - games
                - join <ID> [WHITE|BLACK] - a game
                - observe <ID> - a game
                - logout - when you are done
                - quit
                - help
                """;
    }


}
