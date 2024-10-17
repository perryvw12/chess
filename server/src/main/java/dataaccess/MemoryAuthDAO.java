package dataaccess;

import model.AuthData;

public class MemoryAuthDAO implements AuthDataAccess {

    @Override
    public String createAuth(AuthData authData) throws DataAccessException {
        return "";
    }

    @Override
    public String deleteAuth(String authToken) throws DataAccessException {
        return "";
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }
}
