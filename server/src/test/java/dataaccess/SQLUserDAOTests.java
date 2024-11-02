package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ServiceException;

import java.security.Provider;

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
    void createUserTest() throws ServiceException, DataAccessException {
        var testUser = new UserData("test", "pass", "email");
        dataAccess.userDataAccess.createUser(testUser);
        var actualUser = dataAccess.userDataAccess.getUser("test");
        assertEquals(testUser.username(), actualUser.username());
    }

    @Test
    void createDuplicateUserTest() throws ServiceException, DataAccessException {
        var testUser = new UserData("test", "pass", "email");
        dataAccess.userDataAccess.createUser(testUser);
        var testUser2 = new UserData("test", "pass", "email");
        assertThrows(ServiceException.class, () ->
                dataAccess.userDataAccess.createUser(testUser2));
    }

    @Test
    void goodGetUser() throws ServiceException, DataAccessException {
        var testUser1 = new UserData("test1", "pass1", "email");
        var testUser2 = new UserData("test2", "pass2", "email");
        dataAccess.userDataAccess.createUser(testUser1);
        dataAccess.userDataAccess.createUser(testUser2);
        assertEquals(testUser1.username(), dataAccess.userDataAccess.getUser("test1").username());
    }

    @Test
    void badGetUser() throws ServiceException, DataAccessException {
        var testUser1 = new UserData("test1", "pass1", "email");
        var testUser2 = new UserData("test2", "pass2", "email");
        dataAccess.userDataAccess.createUser(testUser1);
        dataAccess.userDataAccess.createUser(testUser2);
        assertNotEquals(testUser2.username(), dataAccess.userDataAccess.getUser("test1").username());
    }

    @Test
    void clearUserData() throws ServiceException, DataAccessException {
        var testUser1 = new UserData("test1", "pass1", "email");
        var testUser2 = new UserData("test2", "pass2", "email");
        dataAccess.userDataAccess.createUser(testUser1);
        dataAccess.userDataAccess.createUser(testUser2);
        dataAccess.userDataAccess.clearUsers();
        assertNull(dataAccess.userDataAccess.getUser("test1"));
    }
}

