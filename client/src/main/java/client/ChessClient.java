package client;

import server.ServerFacade;

public class ChessClient {
    String authToken = null;
    ServerFacade server;

    public ChessClient(ServerFacade server) {
        this.server = server;
    }
}
