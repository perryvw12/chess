package dataaccess;

import model.UserData;

import javax.xml.crypto.Data;
import java.util.HashMap;

public class MemoryUserDAO implements UserDataAccess {
    HashMap<String, UserData> userStorage = new HashMap<>();

    @Override
    public void createUser(UserData newUser) throws DataAccessException {
        userStorage.put(newUser.username(), newUser);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return userStorage.get(username);
    }

    @Override
    public void clearUsers() throws DataAccessException {
        userStorage = new HashMap<>();
    }
}
