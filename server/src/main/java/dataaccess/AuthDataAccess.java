package dataaccess;

import model.AuthData;

public interface AuthDataAccess {
    String createAuth(AuthData authData) throws DataAccessException;

    String deleteAuth(String authToken) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;
}
