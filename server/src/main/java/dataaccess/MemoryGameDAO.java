package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MemoryGameDAO implements GameDataAccess{
    HashMap<Integer, GameData> gameStorage = new HashMap<>();
    Random IDGenerator = new Random();

    @Override
    public HashMap<String, Integer> createGame(String gameName) throws DataAccessException {
        int gameID = IDGenerator.nextInt(1000);
        GameData gameData = new GameData(gameID, "", "", gameName, new ChessGame());
        gameStorage.put(gameID, gameData);
        HashMap<String, Integer> createGameResult = new HashMap<>();
        createGameResult.put("gameID", gameID);
        return createGameResult;
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
    public void clearGames() throws DataAccessException {
        gameStorage = new HashMap<>();
    }
}
