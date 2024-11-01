package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NewGameServiceTests {
    static DataAccess dataAccess = new DataAccess(DataAccess.Implementation.MEMORY);
    static NewGameService newGameService = new NewGameService(dataAccess);

    @BeforeEach
    void clearTest() throws DataAccessException, ServiceException {
        dataAccess.gameDataAccess.clearGames();
    }

    @Test
    void goodNewGame() throws DataAccessException, ServiceException {
        var authData = dataAccess.authDataAccess.createAuth("testUser");
        newGameService.createGame(authData.authToken(), "testGame");
        assertEquals(1, dataAccess.gameDataAccess.listGames().size());
    }

    @Test
    void badNewGame() throws DataAccessException, ServiceException {
        assertThrows(ServiceException.class, () ->
                newGameService.createGame("badAuthToken", "testGame"));
    }
}
