package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import exception.ServiceException;

import java.util.HashMap;

public class NewGameService {
    DataAccess dataAccess;

    public NewGameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public HashMap<String, Integer> createGame(String authToken, String gameName) throws DataAccessException, ServiceException {
        var authData = dataAccess.authDataAccess.getAuth(authToken);
        if(authData == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }  else {
            return dataAccess.gameDataAccess.createGame(gameName);
        }
    }
}
