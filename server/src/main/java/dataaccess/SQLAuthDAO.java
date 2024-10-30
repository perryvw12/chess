package dataaccess;

import model.AuthData;

public class SQLAuthDAO implements AuthDataAccess{
    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void clearAuth() throws DataAccessException {

    }
}
