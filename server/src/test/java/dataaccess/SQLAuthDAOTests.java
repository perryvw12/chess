package dataaccess;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import exception.ServiceException;
import static dataaccess.DataAccess.configureDatabase;
import static org.junit.jupiter.api.Assertions.*;

public class SQLAuthDAOTests {
    DataAccess dataAccess = new DataAccess(DataAccess.Implementation.SQL);

    @BeforeAll
    static void startup() throws ServiceException, DataAccessException {
        configureDatabase();
    }

    @BeforeEach
    void clearDatabase() throws ServiceException, DataAccessException {
        dataAccess.authDataAccess.clearAuth();
    }

    @Test
    void goodCreateAuth() throws ServiceException, DataAccessException {
        var expectedAuthData = dataAccess.authDataAccess.createAuth("testUser");
        var observedAuthData = dataAccess.authDataAccess.getAuth(expectedAuthData.authToken());
        assertEquals(expectedAuthData, observedAuthData);
    }

    @Test
    void badCreateAuth() throws ServiceException, DataAccessException {
        var expectedAuthData = dataAccess.authDataAccess.createAuth("testUser");
        String badAuthToken = "1";
        assertNull(dataAccess.authDataAccess.getAuth(badAuthToken));
    }

    @Test
    void goodDeleteAuth() throws ServiceException, DataAccessException {
        var expectedAuthData = dataAccess.authDataAccess.createAuth("testUser");
        dataAccess.authDataAccess.deleteAuth(expectedAuthData.authToken());
        assertNull(dataAccess.authDataAccess.getAuth(expectedAuthData.authToken()));
    }

    @Test
    void badDeleteAuth() throws ServiceException, DataAccessException {
        var expectedAuthData = dataAccess.authDataAccess.createAuth("testUser");
        String badAuthToken = "1";
        assertNotNull(dataAccess.authDataAccess.getAuth(expectedAuthData.authToken()));
    }

    @Test
    void goodGetAuth() throws ServiceException, DataAccessException {
        var expectedAuthData = dataAccess.authDataAccess.createAuth("testUser");
        assertEquals(expectedAuthData, dataAccess.authDataAccess.getAuth(expectedAuthData.authToken()));
    }

    @Test
    void badGetAuth() throws ServiceException, DataAccessException {
        var expectedAuthData = dataAccess.authDataAccess.createAuth("testUser");
        var unexpectedAuthData = dataAccess.authDataAccess.createAuth("badUser");
        assertNotEquals(expectedAuthData, dataAccess.authDataAccess.getAuth(unexpectedAuthData.authToken()));
    }

    @Test
    void clearAuthData() throws ServiceException, DataAccessException {
        var expectedAuthData = dataAccess.authDataAccess.createAuth("testUser");
        var unexpectedAuthData = dataAccess.authDataAccess.createAuth("badUser");
        var otherAuthData = dataAccess.authDataAccess.createAuth("badUserWhom");
        dataAccess.authDataAccess.clearAuth();
        assertNull(dataAccess.authDataAccess.getAuth(otherAuthData.authToken()));
    }
}
