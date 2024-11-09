package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import exception.ServiceException;
import model.GameData;

import java.util.ArrayList;

public class ListGameService {
    DataAccess dataAccess;

    public ListGameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public ArrayList<GameData> listGames(String authToken) throws DataAccessException, ServiceException {
        var authData = dataAccess.authDataAccess.getAuth(authToken);
        if(authData == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }  else {
            return dataAccess.gameDataAccess.listGames();
        }
    }
}
