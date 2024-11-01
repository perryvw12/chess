package dataaccess;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ServiceException;

import static dataaccess.DataAccess.configureDatabase;
import static dataaccess.DataAccess.executeUpdate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SQLGameDAOTests {
    DataAccess dataAccess = new DataAccess(DataAccess.Implementation.SQL);

    @BeforeAll
    static void startup() throws ServiceException, DataAccessException {
        configureDatabase();
    }

    @BeforeEach
    void clearDatabase() throws ServiceException, DataAccessException {
        dataAccess.gameDataAccess.clearGames();
    }

    @Test
    void addGame() throws ServiceException, DataAccessException {
        var gameMap = dataAccess.gameDataAccess.createGame("testGame");
        var gameData = dataAccess.gameDataAccess.getGame(gameMap.get("gameID"));
        int gameID = gameMap.get("gameID");
        assertEquals(gameID, gameData.gameID());
    }

    @Test
    void duplicateGame() throws ServiceException, DataAccessException {
        var gameMap = dataAccess.gameDataAccess.createGame("testGame");
        var statement = "INSERT INTO gameData (gameID, json) VALUES (?, ?)";
        assertThrows(ServiceException.class, () ->
        executeUpdate(statement, gameMap.get("gameID"), new Gson().toJson("testData")));
    }
}
