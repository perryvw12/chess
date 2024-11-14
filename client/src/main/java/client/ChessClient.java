package client;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import exception.ServiceException;
import model.GameData;
import model.UserData;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static ui.BoardDrawer.drawBoardBlack;
import static ui.BoardDrawer.drawBoardWhite;

public class ChessClient {
    String authToken = null;
    ServerFacade server;
    ClientState state = ClientState.LOGGEDOUT;
    HashMap<Integer, GameData> gameList = new HashMap<>();


    public ChessClient(ServerFacade server) {
        this.server = server;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if(state == ClientState.PLAYING) {
                return "";
            } else if (state == ClientState.LOGGEDIN) {
                return switch (cmd) {
                    case "create" -> createGame(params);
                    case "list" -> listGames();
                    case "join" -> joinGame(params);
                    case "logout" -> logout();
                    case "observe" -> observeGame(params);
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
            state = ClientState.LOGGEDIN;
            return String.format("You are logged in as %s.%n%s", username, postHelp());
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
            state = ClientState.LOGGEDIN;
            return String.format("You are logged in as %s.%n%s", username, postHelp());
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
        int IdCounter = 1;
        for (GameData gameData : gameDataList) {
            finalResult.append(String.format("Game:%s, ID:%s, WhitePlayer:%s, BlackPlayer:%s%n", gameData.gameName(),
                    IdCounter, gameData.whiteUsername(), gameData.blackUsername()));
            gameList.put(IdCounter++, gameData);
        }
        return finalResult.toString();
    }

    public String joinGame(String... params) throws ServiceException {
        if(params.length >= 2) {
            var gameID = params[0];
            var playerColor = params[1].toUpperCase();
            var gameData = gameList.get(Integer.parseInt(gameID));
            server.joinGame(playerColor, Integer.toString(gameData.gameID()), authToken);
            return String.format("%s%n%s", drawBoardWhite(gameData.chessGame()), drawBoardBlack(gameData.chessGame()));
        }
        throw new ServiceException(400, "Expected: <ID> [WHITE|BLACK]");
    }

    public String logout() throws ServiceException {
        server.logout(authToken);
        authToken = null;
        state = ClientState.LOGGEDOUT;
        return String.format("You have logged out.%n%s", preHelp());
    }

    public String observeGame(String... params) throws ServiceException {
        if(params.length >= 1) {
            var gameID = params[0];
            ChessGame game = (gameList.get(Integer.parseInt(gameID))).chessGame();
            return String.format("%s%n%s", drawBoardWhite(game), drawBoardBlack(game));
        }
        throw new ServiceException(400,"Expected: <ID>");
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

    private enum ClientState {
        LOGGEDOUT,
        LOGGEDIN,
        PLAYING
    }
}
