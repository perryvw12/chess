package server.websocket;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import exception.ServiceException;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import com.google.gson.Gson;
import javax.websocket.*;

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
}
