package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import exception.ServiceException;

public class LogoutService {
    DataAccess dataAccess;

    public LogoutService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void logoutUser(String authToken) throws DataAccessException, ServiceException {
        var authData = dataAccess.authDataAccess.getAuth(authToken);
        if (authData == null) {
            throw new ServiceException(401, "Error: unauthorized");
        } else {
            dataAccess.authDataAccess.deleteAuth(authToken);
        }
    }
}
