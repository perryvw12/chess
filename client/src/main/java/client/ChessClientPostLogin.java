package client;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import exception.ServiceException;
import model.GameData;
import server.ServerFacade;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ChessClientPostLogin {
    String authToken;
    ServerFacade server;
    boolean isPlaying = false;
    HashMap<Integer, ChessGame> gameList = new HashMap<>();

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
                case "logout" -> logout();
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
        Type type = new TypeToken<HashMap<String, ArrayList<GameData>>>(){}.getType();
        HashMap<String, ArrayList<GameData>> deserializedResults = new Gson().fromJson(String.valueOf(results), type);
        ArrayList<GameData> gameDataList = deserializedResults.get("games");
        StringBuilder finalResult = new StringBuilder();
        for (GameData gameData : gameDataList) {
            finalResult.append(String.format("Game:%s, ID:%s, WhitePlayer:%s, BlackPlayer:%s%n", gameData.gameName(), gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername()));
            gameList.put(gameData.gameID(), gameData.chessGame());
        }
        return finalResult.toString();
    }

    public String logout() throws ServiceException {
        server.logout(authToken);
        authToken = null;
        return "You have logged out";
    }

    public String joinGame(String... params) throws ServiceException {
        if(params.length >= 2) {
            var gameID = params[0];
            var playerColor = params[1];
            server.joinGame(playerColor, gameID, authToken);
            isPlaying = true;
        }
        throw new ServiceException(400, "Expected: <ID> [WHITE|BLACK]");
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

    public String getAuth() {
        return authToken;
    }

    public HashMap<Integer, ChessGame> getGameList() {
        return gameList;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

}
