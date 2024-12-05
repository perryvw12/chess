package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataaccess.DataAccess;
import exception.ServiceException;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import com.google.gson.Gson;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;


@WebSocket
public class WebsocketHandler {
    DataAccess dataAccess;
    private final ConnectionManager connections = new ConnectionManager();

    public WebsocketHandler(DataAccess dataAccess) {this.dataAccess = dataAccess;}

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws ServiceException {
       try {
           JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
           String commandType = jsonObject.get("commandType").getAsString();
           String authToken = jsonObject.get("authToken").getAsString();
           Integer gameID = jsonObject.get("gameID").getAsInt();

           switch (commandType) {
               case "CONNECT" -> connect(authToken, session, gameID);
               case "MAKE_MOVE" -> makeMove(message, authToken, gameID, session);
               case "LEAVE" -> leave(message, authToken, gameID);
               case "RESIGN" -> resign(message, authToken, gameID);
           }
       } catch (Exception e) {
           throw new ServiceException(500, e.getMessage());
       }
    }

    private void connect(String authToken, Session session, Integer gameID) throws IOException {
        try {
            connections.saveSession(authToken, session, gameID);
            String username = (dataAccess.authDataAccess.getAuth(authToken)).username();

            ChessGame game = (dataAccess.gameDataAccess.getGame(gameID)).chessGame();
            var loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
            connections.broadcastSelf(authToken, loadGameMessage);

            var message = String.format("%s has joined the game", username);
            var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(authToken, gameID, notification);
        } catch (Exception e) {
            var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Invalid Game");
            connections.broadcastSelf(authToken, errorMessage);
        }
    }

    private void makeMove(String message, String authToken, Integer gameID, Session session) throws IOException {
        try {
            String username = (dataAccess.authDataAccess.getAuth(authToken)).username();
            var command = new Gson().fromJson(message, MakeMoveCommand.class);
            ChessMove chessMove = command.getChessMove();
            GameData gameData = dataAccess.gameDataAccess.getGame(gameID);
            ChessGame game = gameData.chessGame();
             var playerColor = username.equals(gameData.whiteUsername()) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;

            if (!game.isGameInProgress()) {
                var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Game is over no moves can be made");
                connections.broadcastSelf(authToken, errorMessage);
                return;
            }

            if (!playerColor.equals(game.getTeamTurn())) {
                var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Not your turn");
                connections.broadcastSelf(authToken, errorMessage);
                return;
            }

            var validMoves = game.validMoves(chessMove.getStartPosition());
            if (!validMoves.contains(chessMove)) {
                var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Invalid Move");
                connections.broadcastSelf(authToken, errorMessage);
            } else {
                game.makeMove(chessMove);
                GameData updatedGameData = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
                dataAccess.gameDataAccess.updateGame(gameID, updatedGameData);

                var loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
                connections.broadcast(null, gameID, loadGameMessage);

                var moveMessage = String.format("%s has made a move", username);
                var moveNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, moveMessage);
                connections.broadcast(authToken, gameID, moveNotification);

                String statusMessage = null;
                if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
                    statusMessage = String.format("%s has been checkmated, white wins!", gameData.blackUsername());
                    game.setGameInProgress(false);
                    GameData endedGameData = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
                    dataAccess.gameDataAccess.updateGame(gameID, endedGameData);
                } else if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
                    statusMessage = String.format("%s has been checkmated, black wins!", gameData.whiteUsername());
                    game.setGameInProgress(false);
                    GameData endedGameData = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
                    dataAccess.gameDataAccess.updateGame(gameID, endedGameData);
                } else if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
                    statusMessage = String.format("%s is in check", gameData.blackUsername());
                } else if (game.isInCheck(ChessGame.TeamColor.WHITE)) {
                    statusMessage = String.format("%s is in check", gameData.whiteUsername());
                }

                if (statusMessage != null) {
                    var statusNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, statusMessage);
                    connections.broadcast(null, gameID, statusNotification);
                }
            }
        } catch (Exception e) {
            var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Bad Authorization");
            var serialized = new Gson().toJson(errorMessage);
            session.getRemote().sendString(serialized);
        }
    }

    private void leave(String message, String authToken, Integer gameID) throws IOException {
        try {
            String username = (dataAccess.authDataAccess.getAuth(authToken)).username();
            var leaveCommand = new Gson().fromJson(message, UserGameCommand.class);
            connections.deleteSession(authToken);
            GameData gameData = dataAccess.gameDataAccess.getGame(gameID);
            ChessGame.TeamColor playerColor;

            if (username.equals(gameData.whiteUsername())) {
                playerColor = ChessGame.TeamColor.WHITE;
            } else if (username.equals(gameData.blackUsername())) {
                playerColor = ChessGame.TeamColor.BLACK;
            } else {
                playerColor = null;
            }


            if (playerColor != null) {
                if (playerColor.equals(ChessGame.TeamColor.WHITE)) {
                    GameData updatedGame = new GameData(gameID, null, gameData.blackUsername(), gameData.gameName(), gameData.chessGame());
                    dataAccess.gameDataAccess.updateGame(gameID, updatedGame);
                } else if (playerColor.equals(ChessGame.TeamColor.BLACK)) {
                    GameData updatedGame = new GameData(gameID, gameData.whiteUsername(), null, gameData.gameName(), gameData.chessGame());
                    dataAccess.gameDataAccess.updateGame(gameID, updatedGame);
                }
            }

            var notificationMessage = String.format("%s has left the game", username);
            var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, notificationMessage);
            connections.broadcast(username, gameID, notification);
        } catch (Exception e) {
            var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Bad Authorization");
            connections.broadcastSelf(authToken, errorMessage);
        }
    }

    private void resign(String message, String authToken, Integer gameID) throws IOException {
        try {
            String username = (dataAccess.authDataAccess.getAuth(authToken)).username();
            var resignCommand = new Gson().fromJson(message, UserGameCommand.class);
            GameData gameData = dataAccess.gameDataAccess.getGame(gameID);
            ChessGame game = gameData.chessGame();
            ChessGame.TeamColor playerColor;

            if(!game.isGameInProgress()) {
                var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "game is already over");
                connections.broadcastSelf(authToken, errorMessage);
                return;
            }

            if (username.equals(gameData.whiteUsername())) {
                playerColor = ChessGame.TeamColor.WHITE;
            } else if (username.equals(gameData.blackUsername())) {
                playerColor = ChessGame.TeamColor.BLACK;
            } else {
                var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Observer cannot resign");
                connections.broadcastSelf(authToken, errorMessage);
                return;
            }

            String notificationMessage;

            if (playerColor.equals(ChessGame.TeamColor.WHITE)) {
                notificationMessage = "white has resigned, black wins the game!";
            } else {
                notificationMessage = "black has resigned, white wins the game!";
            }
            var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, notificationMessage);
            connections.broadcast(null, gameID, notification);

            game.setGameInProgress(false);
            GameData updatedGameData = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
            dataAccess.gameDataAccess.updateGame(gameID, updatedGameData);
        } catch (Exception e) {
            var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Bad Authorization");
            connections.broadcastSelf(authToken, errorMessage);
        }
    }
}
