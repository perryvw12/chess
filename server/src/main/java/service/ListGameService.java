package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

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
