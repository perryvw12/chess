package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;


import java.util.Objects;

public class ClearService {
    DataAccess dataAccess;

    public ClearService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void deleteAll() throws DataAccessException {
        dataAccess.userDataAccess.deleteUsers();
        dataAccess.authDataAccess.clearAuth();

    }
}
