package dataaccess;
import model.UserData;
import service.ServiceException;

import java.sql.SQLException;

public interface UserDataAccess {
    void createUser(UserData user) throws DataAccessException, ServiceException, SQLException;

    UserData getUser(String username) throws DataAccessException;

    void clearUsers() throws DataAccessException;
}
