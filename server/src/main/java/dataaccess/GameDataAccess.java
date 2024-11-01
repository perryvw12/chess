package dataaccess;
import chess.ChessGame;
import model.GameData;
import service.ServiceException;

import java.util.ArrayList;
import java.util.HashMap;

public interface GameDataAccess {
    HashMap<String, Integer> createGame(String gameName) throws DataAccessException, ServiceException;

    GameData getGame(int gameID) throws DataAccessException, ServiceException;

    ArrayList<GameData> listGames() throws DataAccessException, ServiceException;

    void updateGame(int gameID, GameData updatedGame) throws DataAccessException, ServiceException;

    void clearGames() throws DataAccessException, ServiceException;
}
