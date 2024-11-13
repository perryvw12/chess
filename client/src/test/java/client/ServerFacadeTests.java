package client;

import dataaccess.*;
import exception.ServiceException;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

}
