package server.websocket;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import exception.ServiceException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import com.google.gson.Gson;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;


@WebSocket
public class WebsocketHandler {
    DataAccess dataAccess;
    private final ConnectionManager connections = new ConnectionManager();

    public WebsocketHandler(DataAccess dataAccess) {this.dataAccess = dataAccess;}

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
       try {
           UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
           String username = (dataAccess.authDataAccess.getAuth(command.getAuthToken()).username());
           Integer gameID = command.getGameID();

           switch (command.getCommandType()) {
               case CONNECT -> connect(username, session, gameID);
               case MAKE_MOVE -> makeMove();
               case LEAVE -> leave(username, gameID);
               case RESIGN -> resign();
           }
       } catch (ServiceException | DataAccessException | IOException e) {
           throw new RuntimeException(e);
       }
    }

    private void connect(String username, Session session, Integer gameID) throws IOException {
        connections.saveSession(username, session, gameID);
        var message = String.format("%s has joined the game", username);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(username, gameID, notification);
    }

    private void makeMove() {

    }

    private void leave(String username, Integer gameID) throws IOException {
        connections.deleteSession(username);
        var message = String.format("%s has left the game", username);
        var notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        connections.broadcast(username, gameID, notification);
    }

    private void resign() {

    }
}
