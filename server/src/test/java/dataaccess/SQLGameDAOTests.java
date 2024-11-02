package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ServiceException;

import static dataaccess.DataAccess.configureDatabase;
import static dataaccess.DataAccess.executeUpdate;
import static org.junit.jupiter.api.Assertions.*;

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
    void goodCreateGame() throws ServiceException, DataAccessException {
        var gameMap = dataAccess.gameDataAccess.createGame("testGame");
        var gameData = dataAccess.gameDataAccess.getGame(gameMap.get("gameID"));
        int gameID = gameMap.get("gameID");
        assertEquals(gameID, gameData.gameID());
    }

    @Test
    void badCreateGame() throws ServiceException, DataAccessException {
        var gameMap = dataAccess.gameDataAccess.createGame("testGame");
        var statement = "INSERT INTO gameData (gameID, json) VALUES (?, ?)";
        assertThrows(ServiceException.class, () ->
        executeUpdate(statement, gameMap.get("gameID"), new Gson().toJson("testData")));
    }

    @Test
    void goodListAllGames() throws ServiceException, DataAccessException {
        dataAccess.gameDataAccess.createGame("testGame");
        dataAccess.gameDataAccess.createGame("testGame2");
        dataAccess.gameDataAccess.createGame("testGame3");
        var gameList = dataAccess.gameDataAccess.listGames();
        assertEquals(3, gameList.size());
    }

    @Test
    void badListAllGames() throws ServiceException, DataAccessException {
        dataAccess.gameDataAccess.createGame("testGame");
        dataAccess.gameDataAccess.createGame("testGame2");
        dataAccess.gameDataAccess.createGame("testGame3");
        var gameList = dataAccess.gameDataAccess.listGames();
        dataAccess.gameDataAccess.createGame("unexpectedGame");
        assertNotEquals(4, gameList.size());
    }

    @Test
    void updateGameSuccess() throws ServiceException, DataAccessException {
        var gameMap = dataAccess.gameDataAccess.createGame("testGame");
        var gameID = gameMap.get("gameID");
        var testData = new GameData(gameID, "newUserBlack", null, "testGame", new ChessGame());
        dataAccess.gameDataAccess.updateGame(gameID, testData);
        assertEquals(testData ,dataAccess.gameDataAccess.getGame(gameID));
    }

    @Test
    void updateGameFail() throws ServiceException, DataAccessException {
        var gameMap = dataAccess.gameDataAccess.createGame("testGame");
        var gameID = gameMap.get("gameID");
        var badGameID = 11;
        var testData = new GameData(gameID, "newUserBlack", null, "testGame", new ChessGame());
        dataAccess.gameDataAccess.updateGame(badGameID, testData);
        assertNotEquals(testData, dataAccess.gameDataAccess.getGame(gameID));
    }

    @Test
    void clearGameData() throws ServiceException, DataAccessException {
        dataAccess.gameDataAccess.createGame("testGame");
        dataAccess.gameDataAccess.createGame("testGame2");
        dataAccess.gameDataAccess.createGame("testGame3");
        dataAccess.gameDataAccess.createGame("unexpectedGame");
        dataAccess.gameDataAccess.clearGames();
        var gameList = dataAccess.gameDataAccess.listGames();
        assertEquals(0, gameList.size());
    }
}
