package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void saveSession(String authToken, Session session, Integer gameID) {
        var connection = new Connection(gameID, authToken, session);
        connections.put(authToken, connection);
    }

    public void deleteSession(String authToken) {
        connections.remove(authToken);
    }

    public void broadcast(String excludeAuthToken, Integer gameID, ServerMessage message) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (c.gameID.equals(gameID)) {
                    if (!c.authToken.equals(excludeAuthToken)) {
                        var serializedMessage = new Gson().toJson(message);
                        c.send(serializedMessage);
                    }
                }
            } else {
                removeList.add(c);
            }
        }
        for (var c : removeList) {
            connections.remove(c.authToken);
        }
    }

    public void broadcastSelf(String authToken, ServerMessage message) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (c.authToken.equals(authToken)) {
                    var serializedMessage = new Gson().toJson(message);
                    c.send(serializedMessage);
                }
            } else {
                removeList.add(c);
            }
        }
        for (var c : removeList) {
            connections.remove(c.authToken);
        }
    }
}
