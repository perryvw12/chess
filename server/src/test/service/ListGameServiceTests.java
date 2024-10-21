package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryGameDAO;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class ListGameServiceTests {
    static DataAccess dataAccess = new DataAccess(DataAccess.Implementation.MEMORY);
    static ListGameService listGameService = new ListGameService(dataAccess);

    @BeforeEach
    void addGames() throws DataAccessException {
        dataAccess.gameDataAccess.clearGames();
        dataAccess.gameDataAccess.createGame("testGame2");
        dataAccess.gameDataAccess.createGame("testGame2");
        dataAccess.gameDataAccess.createGame("testGame3");
    }

    @Test
    void goodListGames() throws DataAccessException, ServiceException {
        var authData = dataAccess.authDataAccess.createAuth("testUser");
        var gameList = listGameService.listGames(authData.authToken());
        assertEquals(3, gameList.size());
    }

    @Test
    void badListGames() throws DataAccessException, ServiceException {
        assertThrows(ServiceException.class, () ->
                listGameService.listGames("badAuthToken"));
    }
}
