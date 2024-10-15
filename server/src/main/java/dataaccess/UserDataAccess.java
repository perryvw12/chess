package dataaccess;
import model.UserData;

public interface UserDataAccess {
    UserData createUser(UserData user) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;
}
