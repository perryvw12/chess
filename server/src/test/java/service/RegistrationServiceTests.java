package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RegistrationServiceTests {
    static DataAccess dataAccess = new DataAccess(DataAccess.Implementation.MEMORY);
    static RegistrationService registrationService = new RegistrationService(dataAccess);

    @BeforeEach
    void clearUserData() throws DataAccessException, ServiceException {
        dataAccess.userDataAccess.clearUsers();
    }

    @Test
    void goodRegistration() throws DataAccessException, ServiceException {
        UserData testUser = new UserData("testUser", "password", "test@gmail.com");
        registrationService.registerUser(testUser);
        assertEquals(testUser.username(), dataAccess.userDataAccess.getUser(testUser.username()).username());
    }

    @Test
    void badRegistration() throws DataAccessException, ServiceException {
        UserData testUser1 = new UserData("testUser", "password", "test1@gmail.com");
        UserData testUser2 = new UserData("testUser", "otherPass", "test2@gmail.com");
        dataAccess.userDataAccess.createUser(testUser1);
        assertThrows(ServiceException.class, () ->
                registrationService.registerUser(testUser2));
    }

}
