package server.websocket;

import org.eclipse.jetty.websocket.api.Session;


import java.io.IOException;

public class Connection {
    public Integer gameID;
    public String authToken;
    public Session session;

    public Connection(Integer gameID, String authToken, Session session) {
        this.gameID = gameID;
        this.authToken = authToken;
        this.session = session;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}
