package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import exception.ServiceException;
import model.GameData;

import java.util.Objects;

public class JoinGameService {
    DataAccess dataAccess;

    public JoinGameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void joinGame(String authToken, String playerColor, int gameID) throws DataAccessException, ServiceException {
        var authData = dataAccess.authDataAccess.getAuth(authToken);
        var gameData = dataAccess.gameDataAccess.getGame(gameID);
        if(authData == null) {
            throw new ServiceException(401, "Error: unauthorized");
        }
        if(gameData == null) {
            throw new ServiceException(400, "Error: bad request");
        }
        if(Objects.equals(playerColor, "WHITE")) {
            if(Objects.equals(gameData.whiteUsername(), null)) {
                var updatedGameData = new GameData(gameData.gameID(), authData.username(), gameData.blackUsername(), gameData.gameName(), gameData.chessGame());
                dataAccess.gameDataAccess.updateGame(gameID, updatedGameData);
            } else {
                throw new ServiceException(403, "Error: already taken");
            }
        } else if(Objects.equals(playerColor, "BLACK")) {
            if(Objects.equals(gameData.blackUsername(), null)) {
                var updatedGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), authData.username(), gameData.gameName(), gameData.chessGame());
                dataAccess.gameDataAccess.updateGame(gameID, updatedGameData);
            } else {
                throw new ServiceException(403, "Error: already taken");
            }
        }
    }
}
