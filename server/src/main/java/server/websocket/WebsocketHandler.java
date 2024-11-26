package server.websocket;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import exception.ServiceException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import com.google.gson.Gson;


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
           connections.saveSession(username, session, command.getGameID());

           switch (command.getCommandType()) {
               case CONNECT -> connect();
               case MAKE_MOVE -> makeMove();
               case LEAVE -> leave();
               case RESIGN -> resign();
           }
       } catch (ServiceException | DataAccessException e) {
           throw new RuntimeException(e);
       }
    }

    private void connect() {

    }

    private void makeMove() {

    }

    private void leave() {

    }

    private void resign() {

    }
}
