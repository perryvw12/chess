package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ServiceException;
import org.junit.jupiter.api.Test;

import static dataaccess.DataAccess.configureDatabase;
import static org.junit.jupiter.api.Assertions.*;

public class SQLUserDAOTests {
    DataAccess dataAccess = new DataAccess(DataAccess.Implementation.SQL);

    @BeforeAll
    static void startup() throws ServiceException, DataAccessException {
        configureDatabase();
    }

    @BeforeEach
    void clearDatabase() throws ServiceException, DataAccessException {
        dataAccess.userDataAccess.clearUsers();
    }

    @Test
    void storeUserTest() throws ServiceException, DataAccessException {
        var testUser = new UserData("test", "pass", "email");
        dataAccess.userDataAccess.createUser(testUser);
        var actualUser = dataAccess.userDataAccess.getUser("test");
        assertEquals(testUser, actualUser);
    }

    @Test
    void storeDuplicateUserTest() throws ServiceException, DataAccessException {
        var testUser = new UserData("test", "pass", "email");
        dataAccess.userDataAccess.createUser(testUser);
        var testUser2 = new UserData("test", "pass", "email");
        assertThrows(ServiceException.class, () ->
                dataAccess.userDataAccess.createUser(testUser2));
    }
}

