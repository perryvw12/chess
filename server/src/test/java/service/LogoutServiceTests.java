package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LogoutServiceTests {
    static DataAccess dataAccess = new DataAccess(DataAccess.Implementation.MEMORY);
    static LogoutService logoutService = new LogoutService(dataAccess);

    @BeforeEach
    void clearAuth() throws DataAccessException, ServiceException {
        dataAccess.authDataAccess.clearAuth();
    }

    @Test
    void goodLogout() throws DataAccessException, ServiceException {
        var authData = dataAccess.authDataAccess.createAuth("testUser");
        assertEquals(authData, dataAccess.authDataAccess.getAuth(authData.authToken()));
        logoutService.logoutUser(authData.authToken());
        assertNull(dataAccess.authDataAccess.getAuth(authData.authToken()));
    }

    @Test
    void badLogout() throws DataAccessException, ServiceException {
        assertThrows(ServiceException.class, () ->
                logoutService.logoutUser("badAuthToken"));
    }
}
