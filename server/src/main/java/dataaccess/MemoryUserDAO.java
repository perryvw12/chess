package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import javax.xml.crypto.Data;
import java.util.HashMap;

public class MemoryUserDAO implements UserDataAccess {
    HashMap<String, UserData> userStorage = new HashMap<>();

    @Override
    public void createUser(UserData newUser) throws DataAccessException {
        var hashedPass = BCrypt.hashpw(newUser.password(), BCrypt.gensalt());
        var hashedUser = new UserData(newUser.username(), hashedPass, newUser.email());
        userStorage.put(newUser.username(), hashedUser);
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
