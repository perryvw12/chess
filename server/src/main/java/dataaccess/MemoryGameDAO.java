package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

public class MemoryGameDAO implements GameDataAccess{
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
}
