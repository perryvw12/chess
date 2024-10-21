package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTests {
    static DataAccess dataAccess = new DataAccess(DataAccess.Implementation.MEMORY);
    static ClearService clearService = new ClearService(dataAccess);

    @Test
    void testClear() throws DataAccessException {
        dataAccess.gameDataAccess.createGame("testGame");
        dataAccess.userDataAccess.createUser(new UserData("testUser", "password", "test@gmail.com"));
        var authData = dataAccess.authDataAccess.createAuth("testUser2");
        clearService.deleteAll();
        assertTrue(dataAccess.gameDataAccess.listGames().isEmpty());
        assertNull(dataAccess.userDataAccess.getUser("testUser"));
        assertNull(dataAccess.authDataAccess.getAuth(authData.authToken()));
    }
}
