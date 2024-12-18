package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import client.websocket.ServerMessageObserver;
import client.websocket.WebSocketCommunicator;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import exception.ServiceException;
import model.GameData;
import model.UserData;
import ui.EscapeSequences;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import static ui.BoardDrawer.drawBoardBlack;
import static ui.BoardDrawer.drawBoardWhite;
import static ui.EscapeSequences.*;

public class ChessClient implements ServerMessageObserver {
    String authToken = null;
    ServerFacade server;
    WebSocketCommunicator ws;
    ClientState state = ClientState.LOGGEDOUT;
    ChessGame.TeamColor playerColor = null;
    Integer currentGameID = null;
    ChessGame currentGame = null;
    HashMap<Integer, GameData> gameList = new HashMap<>();


    public ChessClient(ServerFacade server) {
        this.server = server;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (state == ClientState.OBSERVING) {
                return switch (cmd) {
                    case "redraw" -> redrawBoard();
                    case "leave" -> leaveGame();
                    case "highlight" -> highlightMoves(params);
                    default -> observingHelp();
                };
            } else if(state == ClientState.PLAYING) {
                return switch (cmd) {
                    case "redraw" -> redrawBoard();
                    case "leave" -> leaveGame();
                    case "move" -> movePiece(params);
                    case "highlight" -> highlightMoves(params);
                    case "resign" -> resign();
                    default -> playingHelp();
                };
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
        int numCounter = 1;
        for (GameData gameData : gameDataList) {
            finalResult.append(String.format("#%s Name:%s, WhitePlayer:%s, BlackPlayer:%s%n", numCounter, gameData.gameName(),
                    gameData.whiteUsername() != null ? gameData.whiteUsername() : "[open]",
                    gameData.blackUsername() != null ? gameData.blackUsername() : "[open]"));
            gameList.put(numCounter++, gameData);
        }
        return finalResult.toString();
    }

    public String joinGame(String... params) throws ServiceException {
        if(params.length >= 2) {
            try {
                var gameID = Integer.parseInt(params[0]);
                var gameData = gameList.get(gameID);
                boolean whiteEmpty = gameData.whiteUsername()==null;
                boolean blackEmpty = gameData.blackUsername()==null;
                var playerColor = params[1].toUpperCase();
                if(!playerColor.equals("WHITE") & !playerColor.equals("BLACK")) {
                    return "Invalid option. Please pick white or black.";
                }
                if((playerColor.equals("BLACK") & !blackEmpty) | (playerColor.equals("WHITE") & !whiteEmpty)) {
                    return "Color is already taken";
                }
                server.joinGame(playerColor, Integer.toString(gameData.gameID()), authToken);
                this.playerColor = playerColor.equals("WHITE") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
                currentGameID = gameData.gameID();
                currentGame = gameData.chessGame();
                state = ClientState.PLAYING;
                ws = new WebSocketCommunicator(server.serverUrl, this);
                ws.connect(authToken, currentGameID, this.playerColor);
                return "";
            } catch (Exception e) {
                throw new ServiceException(400, "Invalid game number. Please provide a valid number.");
            }
        }
        throw new ServiceException(400, "Expected: <GameNumber> [WHITE|BLACK]");
    }

    public String logout() throws ServiceException {
        server.logout(authToken);
        authToken = null;
        state = ClientState.LOGGEDOUT;
        return String.format("You have logged out.%n%s", preHelp());
    }

    public String observeGame(String... params) throws ServiceException {
        if(params.length >= 1) {
            var gameNum = Integer.parseInt(params[0]);
            var gameData = gameList.get(gameNum);
            currentGame = gameData.chessGame();
            currentGameID = gameData.gameID();
            state = ClientState.OBSERVING;

            ws = new WebSocketCommunicator(server.serverUrl, this);
            ws.connect(authToken, currentGameID, null);
            return "";
        }
        throw new ServiceException(400,"Expected: <GameNumber>");
    }

    public String postHelp() {
        return """
                - create <NAME> - creates a game
                - list - games
                - join <GameNumber> [WHITE|BLACK] - a game
                - observe <GameNumber> - a game
                - logout - when you are done
                - quit
                - help
                """;
    }

    public String redrawBoard() {
        return playerColor == ChessGame.TeamColor.BLACK ? drawBoardBlack(currentGame, null) : drawBoardWhite(currentGame, null);
    }

    public String leaveGame() throws ServiceException {
        ws.leaveGame(authToken, currentGameID, playerColor);
        ws = null;
        state = ClientState.LOGGEDIN;
        return "You have left the game";
    }

    public String movePiece(String... params) throws ServiceException {
        try {
            if (params.length >= 2) {
                ChessPiece.PieceType promotion = null;
                String startCoordinates = params[0];
                char startCol = Character.toLowerCase(startCoordinates.charAt(0));
                int startColNum = startCol - 'a' + 1;
                int startRowNum = Integer.parseInt(startCoordinates.substring(1));
                ChessPosition piecePosition = new ChessPosition(startRowNum, startColNum);

                String endCoordinates = params[1];
                char endCol = Character.toLowerCase(endCoordinates.charAt(0));
                int endColNum = endCol - 'a' + 1;
                int endRowNum = Integer.parseInt(endCoordinates.substring(1));
                ChessPosition movePosition = new ChessPosition(endRowNum, endColNum);

                if (params.length >= 3) {
                    String pieceType = (params[2]).toLowerCase();
                    promotion = switch (pieceType) {
                        case "queen" -> ChessPiece.PieceType.QUEEN;
                        case "rook" -> ChessPiece.PieceType.ROOK;
                        case "bishop" -> ChessPiece.PieceType.BISHOP;
                        case "knight" -> ChessPiece.PieceType.KNIGHT;
                        default -> null;
                    };
                }

                ChessMove chessMove = new ChessMove(piecePosition, movePosition, promotion);
                ws.makeMove(authToken, currentGameID, chessMove, playerColor);
                return "";
            }
            throw new ServiceException(400, "Invalid input");
        } catch (Exception e) {
            return "invalid input";
        }

    }

    public String highlightMoves(String... params) throws ServiceException {
        if (params.length >= 1) {
            try {
                String coordinates = params[0];
                char column = Character.toLowerCase(coordinates.charAt(0));
                int colNum = column - 'a' + 1;
                int rowNum = Integer.parseInt(coordinates.substring(1));
                ChessPosition chessPosition = new ChessPosition(rowNum, colNum);
                return playerColor == ChessGame.TeamColor.BLACK ?
                        drawBoardBlack(currentGame, chessPosition) : drawBoardWhite(currentGame, chessPosition);
            } catch (Exception e) {
                throw new ServiceException(400,"Expected: <piece location i.e. a4>");
            }
        }
        throw new ServiceException(400,"Expected: <piece location i.e. a4>");
    }

    public String resign() throws ServiceException {
        System.out.printf("%s%s%n", EscapeSequences.SET_TEXT_COLOR_RED, "Are you sure you want to resign? type yes to confirm or no to cancel");
        Scanner scanner = new Scanner(System.in);
        var line = scanner.nextLine();
        line = line.toLowerCase();
        if(line.equals("yes")) {
            ws.resignGame(authToken, currentGameID, playerColor);
            return "";
        } else {
            return "resign cancelled";
        }
    }

    public String playingHelp() {
        return """
                - redraw - redraws the chess board
                - leave - leaves the game
                - move <Piece position i.e. a4> <position to move to i.e. a5> <Optional Promotion piece for pawn i.e. queen> - moves a chess piece
                - highlight <piece location i.e. a4> - shows the possible moves for a piece
                - resign - forfeits the game
                - help - shows list of available commands
                """;
    }

    public String observingHelp() {
        return """
                - redraw - redraws the chess board
                - leave - leaves the game
                - help - shows list of available commands
                - highlight <piece location i.e. a4> - shows the possible moves for a piece
                """;
    }

    @Override
    public void notify(String message) {
        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
        String messageType = jsonObject.get("serverMessageType").getAsString();
        switch (messageType) {
            case "NOTIFICATION":
                var notification = new Gson().fromJson(message, NotificationMessage.class);
                System.out.printf("%s%s%n", SET_TEXT_COLOR_GREEN, notification.getMessage());
                System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
                break;

            case "LOAD_GAME":
                var loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
                currentGame = loadGameMessage.getGame();
                if (playerColor == ChessGame.TeamColor.BLACK) {
                    System.out.printf("%n%s%s%n", SET_TEXT_COLOR_BLUE, drawBoardBlack(currentGame, null));
                    System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
                } else {
                    System.out.printf("%n%s%s%n", SET_TEXT_COLOR_BLUE, drawBoardWhite(currentGame, null));
                    System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
                }
                break;

            case "ERROR":
                var error = new Gson().fromJson(message, ErrorMessage.class);
                System.out.printf("%s%s%n", EscapeSequences.SET_TEXT_COLOR_RED, error.getErrorMessage());
                System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
                break;
        }
    }

    private enum ClientState {
        LOGGEDOUT,
        LOGGEDIN,
        PLAYING,
        OBSERVING
    }
}
