package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MemoryGameDAO implements GameDataAccess{
    HashMap<Integer, GameData> gameStorage = new HashMap<>();
    Random idGenerator = new Random();

    @Override
    public HashMap<String, Integer> createGame(String gameName) throws DataAccessException {
        int gameID = idGenerator.nextInt(1000);
        GameData gameData = new GameData(gameID, null, null, gameName, new ChessGame());
        gameStorage.put(gameID, gameData);
        HashMap<String, Integer> createGameResult = new HashMap<>();
        createGameResult.put("gameID", gameID);
        return createGameResult;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return gameStorage.get(gameID);
    }

    @Override
    public ArrayList<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(gameStorage.values());
    }

    @Override
    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException {
        gameStorage.put(gameID, updatedGame);
    }

    @Override
    public void clearGames() throws DataAccessException {
        gameStorage = new HashMap<>();
    }
}
