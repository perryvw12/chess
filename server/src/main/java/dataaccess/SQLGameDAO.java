package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;

public class SQLGameDAO implements GameDataAccess{
    @Override
    public HashMap<String, Integer> createGame(String gameName) throws DataAccessException {
        return null;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException {

    }

    @Override
    public void clearGames() throws DataAccessException {

    }
}
