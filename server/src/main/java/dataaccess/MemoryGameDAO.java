package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;

public class MemoryGameDAO implements GameDataAccess{
    HashMap<String, AuthData> gameStorage = new HashMap<>();

    @Override
    public String createGame(GameData gameData) throws DataAccessException {
        return "";
    }

    @Override
    public GameData getGame(String gameID) throws DataAccessException {
        return null;
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        return null;
    }

    @Override
    public boolean updateGame(String gameID, ChessGame game) throws DataAccessException {
        return false;
    }

    @Override
    public void deleteGames() throws DataAccessException {
        gameStorage = new HashMap<>();
    }
}
