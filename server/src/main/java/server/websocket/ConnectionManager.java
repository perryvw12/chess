package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void saveSession(String username, Session session, Integer gameID) {
        var connection = new Connection(gameID, username, session);
        connections.put(username, connection);
    }

    public void deleteSession(String username) {
        connections.remove(username);
    }

    public void broadcast(String excludeUsername, Integer gameID, ServerMessage message) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (c.gameID.equals(gameID)) {
                    if (!c.username.equals(excludeUsername)) {
                        c.send(message.toString());
                    }
                }
            } else {
                removeList.add(c);
            }
        }
        for (var c : removeList) {
            connections.remove(c.username);
        }
    }
}
