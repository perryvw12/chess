package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
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
           String username = (dataAccess.authDataAccess.getAuth(authToken).username());
           Integer gameID = jsonObject.get("gameID").getAsInt();

           switch (commandType) {
               case "CONNECT" -> connect(username, session, gameID);
               case "MAKE_MOVE" -> makeMove(message, username, gameID);
               case "LEAVE" -> leave(message, username, gameID);
               case "RESIGN" -> resign(message, username, gameID);
           }
       } catch (ServiceException | DataAccessException | IOException | InvalidMoveException e) {
           throw new ServiceException(500, e.getMessage());
       }
    }

    private void connect(String username, Session session, Integer gameID) throws IOException, ServiceException, DataAccessException {
        connections.saveSession(username, session, gameID);

        ChessGame game = (dataAccess.gameDataAccess.getGame(gameID)).chessGame();
        var loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        connections.broadcastSelf(username, loadGameMessage);

        var message = String.format("%s has joined the game", username);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(username, gameID, notification);
    }

    private void makeMove(String message, String username, Integer gameID) throws ServiceException, DataAccessException, IOException, InvalidMoveException {
        var command = new Gson().fromJson(message, MakeMoveCommand.class);
        ChessMove chessMove = command.getChessMove();
        GameData gameData = dataAccess.gameDataAccess.getGame(gameID);
        ChessGame game = gameData.chessGame();
        var playerColor = command.getPlayerColor();

        if (game.isInCheckmate(ChessGame.TeamColor.WHITE) | game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
            var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Game is over no moves can be made");
            connections.broadcastSelf(username, errorMessage);
        }

        if(!playerColor.equals(game.getTeamTurn())) {
            var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Not your turn");
            connections.broadcastSelf(username, errorMessage);
        }

        var validMoves = game.validMoves(chessMove.getStartPosition());
        if (!validMoves.contains(chessMove)) {
            var errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Invalid Move");
            connections.broadcastSelf(username, errorMessage);
        } else {
            game.makeMove(chessMove);
            GameData updatedGameData = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
            dataAccess.gameDataAccess.updateGame(gameID, updatedGameData);

            var loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
            connections.broadcast(null, gameID, loadGameMessage);

            var moveMessage = String.format("%s has made a move", username);
            var moveNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, moveMessage);
            connections.broadcast(username, gameID, moveNotification);

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
    }

    private void leave(String message, String username, Integer gameID) throws IOException, ServiceException, DataAccessException {
        var leaveCommand = new Gson().fromJson(message, UserGameCommand.class);
        var playerColor = leaveCommand.getPlayerColor();
        connections.deleteSession(username);

        GameData gameData = dataAccess.gameDataAccess.getGame(gameID);
        if(playerColor.equals(ChessGame.TeamColor.WHITE)) {
            GameData updatedGame = new GameData(gameID,null, gameData.blackUsername(), gameData.gameName(), gameData.chessGame());
            dataAccess.gameDataAccess.updateGame(gameID, updatedGame);
        } else if (playerColor.equals(ChessGame.TeamColor.BLACK)) {
            GameData updatedGame = new GameData(gameID, gameData.whiteUsername(), null, gameData.gameName(), gameData.chessGame());
            dataAccess.gameDataAccess.updateGame(gameID, updatedGame);
        }

        var notificationMessage = String.format("%s has left the game", username);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, notificationMessage);
        connections.broadcast(username, gameID, notification);
    }

    private void resign(String message, String username, Integer gameID) throws ServiceException, DataAccessException, IOException {
        var resignCommand = new Gson().fromJson(message, UserGameCommand.class);
        var playerColor = resignCommand.getPlayerColor();
        GameData gameData = dataAccess.gameDataAccess.getGame(gameID);
        ChessGame game = gameData.chessGame();
        String notificationMessage = "";

        if(playerColor.equals(ChessGame.TeamColor.WHITE)) {
            notificationMessage = "white has resigned, black wins the game!";
        } else {
            notificationMessage = "black has resigned, white wins the game!";
        }
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, notificationMessage);
        connections.broadcast(null, gameID, notification);

        game.setGameInProgress(false);
        GameData updatedGameData = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
        dataAccess.gameDataAccess.updateGame(gameID, updatedGameData);
    }
}
