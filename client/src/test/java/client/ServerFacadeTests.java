package client;

import dataaccess.*;
import exception.ServiceException;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void goodRegister() throws ServiceException, DataAccessException {
        ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
        serverFacade.clearAll();
        var testUser = new UserData("test", "test", "email");
        var authData = serverFacade.registerUser(testUser);
        assertEquals(testUser.username(), authData.username());
    }

    @Test
    public void badRegister() throws ServiceException, DataAccessException {
        ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
        serverFacade.clearAll();
        var testUser = new UserData("test", "test", "email");
        serverFacade.registerUser(testUser);
        var badUser = new UserData("test", "test", "badTest");
        assertThrows(ServiceException.class, () ->
                serverFacade.registerUser(badUser));
    }

    @Test
    public void goodLogin() throws ServiceException {
        ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
        serverFacade.clearAll();
        var testUser = new UserData("test", "test", "email");
        var expectedAuth = serverFacade.registerUser(testUser);
        HashMap<String, String> loginReq = new HashMap<>();
        loginReq.put("username", testUser.username());
        loginReq.put("password", testUser.password());
        var observedAuth = serverFacade.loginUser(loginReq);
        assertEquals(observedAuth.username(), expectedAuth.username());
    }

    @Test
    public void badLogin() throws ServiceException {
        ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
        serverFacade.clearAll();
        var testUser = new UserData("test", "test", "email");
        HashMap<String, String> loginReq = new HashMap<>();
        loginReq.put("username", testUser.username());
        loginReq.put("password", testUser.password());
        assertThrows(ServiceException.class, () ->
                serverFacade.loginUser(loginReq));
    }

    @Test
    public void goodCreate() throws ServiceException {
        ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
        ChessClient client = new ChessClient(serverFacade);
        serverFacade.clearAll();
        var testUser = new UserData("test", "test", "email");
        var authData = serverFacade.registerUser(testUser);
        HashMap<String, String> createReq = new HashMap<>();
        createReq.put("authorization", authData.authToken());
        createReq.put("gameName", "testGame");
        serverFacade.createGame(createReq);
        client.authToken = authData.authToken();
        assertNotNull(client.listGames());
    }

    @Test
    public void badCreate() throws ServiceException {
        ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
        ChessClient client = new ChessClient(serverFacade);
        serverFacade.clearAll();
        var testUser = new UserData("test", "test", "email");
        var authData = serverFacade.registerUser(testUser);
        HashMap<String, String> createReq = new HashMap<>();
        createReq.put("authorization", "badAUth");
        createReq.put("gameName", "testGame");
        assertThrows(ServiceException.class, () ->
                serverFacade.createGame(createReq));
    }

    @Test
    public void goodLogout() throws ServiceException {
        ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
        serverFacade.clearAll();
        var testUser = new UserData("test", "test", "email");
        var authData = serverFacade.registerUser(testUser);
        serverFacade.logout(authData.authToken());
        assertThrows(ServiceException.class, () ->
                serverFacade.logout(authData.authToken()));
    }

    @Test
    public void badLogout() throws ServiceException {
        ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
        serverFacade.clearAll();
        var testUser = new UserData("test", "test", "email");
        serverFacade.registerUser(testUser);
        assertThrows(ServiceException.class, () ->
                serverFacade.logout("badAuthToken"));
    }

    @Test
    public void goodList() throws ServiceException {
        ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
        serverFacade.clearAll();
        var testUser = new UserData("test", "test", "email");
        var authData = serverFacade.registerUser(testUser);
        ChessClient client = new ChessClient(serverFacade);
        client.authToken = authData.authToken();
        assertEquals(client.listGames(), client.listGames());
        client.createGame("game1");
        client.createGame("game2");
        client.createGame("game3");
        assertEquals(client.listGames(), client.listGames());
    }

    @Test
    public void badList() throws ServiceException {
        ServerFacade serverFacade = new ServerFacade("http://localhost:8080");
        serverFacade.clearAll();
        var testUser = new UserData("test", "test", "email");
        var authData = serverFacade.registerUser(testUser);
        ChessClient client = new ChessClient(serverFacade);
        client.authToken = authData.authToken();
        client.createGame("game1");
        client.createGame("game2");
        client.createGame("game3");
        client.authToken = "badAuth";
        assertThrows(ServiceException.class, client::listGames);
    }
}
