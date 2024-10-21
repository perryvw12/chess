package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JoinGameServiceTests {
    static DataAccess dataAccess = new DataAccess(DataAccess.Implementation.MEMORY);
    static JoinGameService joinGameService = new JoinGameService(dataAccess);

    @BeforeAll
    static void addGame() throws DataAccessException {
        dataAccess.gameDataAccess.clearGames();
    }

    @Test
    void goodJoin() throws DataAccessException, ServiceException {
        var gameID = dataAccess.gameDataAccess.createGame("testGame").get("gameID");
        var authData = dataAccess.authDataAccess.createAuth("testUser");
        joinGameService.joinGame(authData.authToken(), "WHITE", gameID);
        var gameData = dataAccess.gameDataAccess.getGame(gameID);
        assertEquals(authData.username(), gameData.whiteUsername());
    }

    @Test
    void badJoin() throws DataAccessException, ServiceException {
        var gameID = dataAccess.gameDataAccess.createGame("testGame").get("gameID");
        var testUser1 = dataAccess.authDataAccess.createAuth("testUser");
        var testUser2 = dataAccess.authDataAccess.createAuth("testUser");
        joinGameService.joinGame(testUser1.authToken(),"WHITE", gameID);
        assertThrows(ServiceException.class, () ->
                joinGameService.joinGame(testUser2.authToken(),"WHITE", gameID));
    }

}
