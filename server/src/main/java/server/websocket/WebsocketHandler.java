package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import exception.ServiceException;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import com.google.gson.Gson;
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
           UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
           String username = (dataAccess.authDataAccess.getAuth(command.getAuthToken()).username());
           Integer gameID = command.getGameID();

           switch (command.getCommandType()) {
               case CONNECT -> connect(username, session, gameID);
               case MAKE_MOVE -> makeMove(command, username, gameID);
               case LEAVE -> leave(username, gameID);
               case RESIGN -> resign();
           }
       } catch (ServiceException | DataAccessException | IOException e) {
           throw new ServiceException(500, e.getMessage());
       }
    }

    private void connect(String username, Session session, Integer gameID) throws IOException, ServiceException, DataAccessException {
        connections.saveSession(username, session, gameID);

        ChessGame game = (dataAccess.gameDataAccess.getGame(gameID)).chessGame();
        var loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        connections.broadcast(null, gameID, loadGameMessage);

        var message = String.format("%s has joined the game", username);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(username, gameID, notification);
    }

    private void makeMove(UserGameCommand command, String username, Integer gameID) throws ServiceException, DataAccessException, IOException {
        ChessMove chessMove = ((MakeMoveCommand) command).getChessMove();
        GameData gameData = dataAccess.gameDataAccess.getGame(gameID);
        ChessGame game = gameData.chessGame();

        try {
            game.makeMove(chessMove);
        } catch (InvalidMoveException e) {
            throw new RuntimeException(e);
        }
        GameData updatedGame = new GameData(gameID, gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
        dataAccess.gameDataAccess.updateGame(gameID, updatedGame);

        var loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        connections.broadcast(null, gameID, loadGameMessage);

        var moveMessage = String.format("piece was moved from %s to %s", chessMove.getStartPosition(), chessMove.getEndPosition());
        var moveNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, moveMessage);
        connections.broadcast(username, gameID, moveNotification);

        String statusMessage = null;
        if(game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
            statusMessage = String.format("%s has been checkmated", gameData.blackUsername());
        } else if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
            statusMessage = String.format("%s has been checkmated", gameData.whiteUsername());
        } else if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
            statusMessage = String.format("%s is in check", gameData.blackUsername());
        } else if (game.isInCheck(ChessGame.TeamColor.WHITE)) {
            statusMessage = String.format("%s is in check", gameData.whiteUsername());
        }

        if(statusMessage != null) {
            var statusNotification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, statusMessage);
            connections.broadcast(null, gameID, statusNotification);
        }
    }

    private void leave(String username, Integer gameID) throws IOException, ServiceException, DataAccessException {
        connections.deleteSession(username);

        GameData gameData = dataAccess.gameDataAccess.getGame(gameID);
        if(gameData.whiteUsername().equals(username)) {
            GameData updatedGame = new GameData(gameID,null, gameData.blackUsername(), gameData.gameName(), gameData.chessGame());
            dataAccess.gameDataAccess.updateGame(gameID, updatedGame);
        } else if (gameData.blackUsername().equals(username)) {
            GameData updatedGame = new GameData(gameID, gameData.whiteUsername(), null, gameData.gameName(), gameData.chessGame());
            dataAccess.gameDataAccess.updateGame(gameID, updatedGame);
        }

        var message = String.format("%s has left the game", username);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(username, gameID, notification);
    }

    private void resign() {

    }
}
