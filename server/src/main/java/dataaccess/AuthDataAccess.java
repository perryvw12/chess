package dataaccess;

import model.AuthData;

public interface AuthDataAccess {
    AuthData createAuth(String username) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void clearAuth() throws DataAccessException;
}
