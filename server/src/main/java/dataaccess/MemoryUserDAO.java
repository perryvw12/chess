package dataaccess;

import model.UserData;
import service.UserServices;

import java.util.HashMap;

public class MemoryUserDAO implements UserDataAccess {
    HashMap<String, UserData> userStorage = new HashMap<>();

    @Override
    public String createUser(UserData newUser) throws DataAccessException {
        userStorage.put(newUser.username(), newUser);
        return "200";
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return userStorage.get(username);
    }
}
