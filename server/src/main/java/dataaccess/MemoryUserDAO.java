package dataaccess;

import model.UserData;

public class MemoryUserDAO implements UserDataAccess {
    @Override
    public UserData createUser(UserData user) throws DataAccessException {
        return null;
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }
}
