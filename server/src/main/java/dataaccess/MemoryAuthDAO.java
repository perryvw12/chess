package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Random;

public class MemoryAuthDAO implements AuthDataAccess {
    HashMap<String, AuthData> authStorage = new HashMap<>();
    int tokenCount = 0;
    Random randomToken = new Random();

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        int authToken = randomToken.nextInt(1000);
        AuthData newAuth = new AuthData(Integer.toString(authToken), username);
        authStorage.put(Integer.toString(authToken), newAuth);
        return newAuth;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        authStorage.remove(authToken);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return authStorage.get(authToken);
    }

    @Override
    public void clearAuth() throws DataAccessException {
        authStorage = new HashMap<>();
    }
}
