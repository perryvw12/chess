package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;

public class ClearService {
    DataAccess dataAccess;

    public ClearService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void deleteAll() throws DataAccessException, ServiceException {
        dataAccess.userDataAccess.clearUsers();
        dataAccess.authDataAccess.clearAuth();
        dataAccess.gameDataAccess.clearGames();
    }
}
