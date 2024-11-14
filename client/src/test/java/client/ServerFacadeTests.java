package client;

import dataaccess.*;
import exception.ServiceException;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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


}
