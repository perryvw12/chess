package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDataAccess {
    HashMap<String, AuthData> authStorage = new HashMap<>();
    int tokenCount = 0;

    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        tokenCount += 1;
        AuthData newAuth = new AuthData(Integer.toString(tokenCount), username);
        authStorage.put(Integer.toString(tokenCount), newAuth);
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
