package client;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import exception.ServiceException;
import model.GameData;
import model.UserData;
import server.ServerFacade;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ChessClient {
    String authToken = null;
    ServerFacade server;
    clientState state = clientState.LOGGEDOUT;
    HashMap<Integer, ChessGame> gameList;


    public ChessClient(ServerFacade server) {
        this.server = server;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if(state == clientState.PLAYING) {
                return "";
            } else if (state == clientState.LOGGEDIN) {
                return switch (cmd) {
                    case "create" -> createGame(params);
                    case "list" -> listGames();
                    case "join" -> joinGame();
                    case "logout" -> logout();
                    case "quit" -> "quit";
                    default -> postHelp();
                };
            } else {
                return switch (cmd) {
                    case "register" -> register(params);
                    case "login" -> login(params);
                    case "quit" -> "quit";
                    default -> preHelp();
                };
            }
        } catch (ServiceException ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws ServiceException {
        if (params.length >= 3) {
            var username = params[0];
            var password = params[1];
            var email = params[2];
            UserData newUser = new UserData(username, password, email);
            var authData = server.registerUser(newUser);
            authToken = authData.authToken();
            state = clientState.LOGGEDIN;
            return String.format("You are logged in as %s.%n", username);
        }
        throw new ServiceException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String login(String... params) throws ServiceException {
        if (params.length >= 2) {
            var username = params[0];
            var password = params[1];
            HashMap<String, String> loginReq = new HashMap<>();
            loginReq.put("username", username);
            loginReq.put("password", password);
            var authData = server.loginUser(loginReq);
            authToken = authData.authToken();
            state = clientState.LOGGEDIN;
            return String.format("You are logged in as %s.%n", username);
        }
        throw new ServiceException(400, "Expected: <USERNAME> <PASSWORD>");
    }

    public String preHelp() {
        return """
                - register <USERNAME> <PASSWORD> <EMAIL>
                - login <USERNAME> <PASSWORD>
                - quit
                - help
                """;
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

    public String joinGame(String... params) throws ServiceException {
        if(params.length >= 2) {
            var gameID = params[0];
            var playerColor = params[1];
            server.joinGame(playerColor, gameID, authToken);
        }
        throw new ServiceException(400, "Expected: <ID> [WHITE|BLACK]");
    }

    public String logout() throws ServiceException {
        server.logout(authToken);
        authToken = null;
        state = clientState.LOGGEDOUT;
        return "You have logged out";
    }

    public String postHelp() {
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

    private enum clientState {
        LOGGEDOUT,
        LOGGEDIN,
        PLAYING
    }
}
