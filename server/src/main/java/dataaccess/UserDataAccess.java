package dataaccess;
import model.UserData;

public interface UserDataAccess {
    String createUser(UserData user) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;
}
