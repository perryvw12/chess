package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTests {
    static DataAccess dataAccess = new DataAccess(DataAccess.Implementation.MEMORY);
    static LoginService loginService = new LoginService(dataAccess);

    @BeforeAll
    static void addUserTestData() throws DataAccessException, ServiceException {
        var testUser = new UserData("testUser", "password", "g@mail");
        dataAccess.userDataAccess.createUser(testUser);
    }

    @Test
    void successfulLogin() throws ServiceException, DataAccessException {
        var expectedAuthData = loginService.loginUser("testUser", "password");
        var actualAuthData = dataAccess.authDataAccess.getAuth(expectedAuthData.authToken());
        assertEquals(expectedAuthData, actualAuthData);
    }

    @Test
    void badPassword() {
        assertThrows(ServiceException.class, () ->
                loginService.loginUser("testUser", "badPass"));
    }

}
