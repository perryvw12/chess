package dataaccess;
import model.UserData;

public interface UserDataAccess {
    void createUser(UserData user) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void deleteUsers() throws DataAccessException;
}
