package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Connection> connections = new ConcurrentHashMap<>();

    public void saveSession(String visitorName, Session session, Integer gameID) {
        var connection = new Connection(visitorName, session);
        connections.put(gameID, connection);
    }

    public void deleteSession(Integer gameID) {
        connections.remove(gameID);
    }

    public void broadcast(String excludeVisitorName, Integer gameID, ServerMessage message) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.visitorName.equals(excludeVisitorName)) {
                    c.send(message.toString());
                }
            } else {
                removeList.add(c);
            }
        }
        for (var c : removeList) {
            connections.remove(gameID, c);
        }
    }
}
